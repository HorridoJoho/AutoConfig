package de.autoconfig;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.annotation.AutoConfigObject;
import de.autoconfig.parser.IAutoConfigParser;
import de.autoconfig.parser.NullParser;
import de.autoconfig.validator.IAutoConfigValidator;
import de.autoconfig.validator.NullValidator;

public final class AutoConfig
{
	// Patterns for one dim arrays
	// ^\[((\\;|\\\[|\\\]|[^;\[\]])*(;(\\;|\\\[|\\\]|[^;\[\]])*)*)?\]$
	private static final Pattern PATTERN_ARR_ONE_DIM = Pattern.compile("^\\[((\\\\;|\\\\\\[|\\\\\\]|[^;\\[\\]])*(;(\\\\;|\\\\\\[|\\\\\\]|[^;\\[\\]])*)*)?\\]$");
	
	// Patterns for two dim arrays
	// ^\[\s*((\[((\\;|\\\[|\\\]|[^;\[\]])*(;(\\;|\\\[|\\\]|[^;\[\]])*)*)?\]\s*)*)\]$
	private static final Pattern PATTERN_ARR_TWO_DIM = Pattern.compile("^\\[\\s*((\\[((\\\\;|\\\\\\[|\\\\\\]|[^;\\[\\]])*(;(\\\\;|\\\\\\[|\\\\\\]|[^;\\[\\]])*)*)?\\]\\s*)*)\\]$");
	// \]\s*\[
	private static final Pattern PATTERN_SPLIT_ONE_DIMS = Pattern.compile("\\]\\s*\\[");

	// Patterns for element split
	// (?<!\\);
	private static final Pattern PATTERN_SPLIT_ARR_ELEMENTS = Pattern.compile("(?<!\\\\);");

	// Escapes pattern
	// \\(\\|\[|;|\])
	private static final Pattern PATTERN_ELEMENT_ESCAPES = Pattern.compile("\\\\(\\\\|\\[|;|\\])");
	
	private static void assignField(Object instance, Field field, Object value) throws Exception
	{
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible)
		{
			field.setAccessible(true);
		}
		field.set(instance, value);
		if (!wasAccessible)
		{
			field.setAccessible(false);
		}
	}

	private static Object doValueOfConversion(String value, Class<?> type) throws Exception
	{
		if (type == String.class)
			return value;

		if (type.isPrimitive())
		{
			if (type == byte.class)
				type = Byte.class;
			else if (type == short.class)
				type = Short.class;
			else if (type == int.class)
				type = Integer.class;
			else if (type == long.class)
				type = Long.class;
			else if (type == float.class)
				type = Float.class;
			else if (type == double.class)
				type = Double.class;
			else if (type == boolean.class)
				type = Boolean.class;
			else if (type == char.class)
				throw new Exception("Character type properties are not supported!");
			else
				throw new Exception("Missing primitive type check!");

			value = value.trim();
		}

		return type.getMethod("valueOf", String.class).invoke(null, value);
	}

	/**
	 * Fill a one dimensional array.
	 * 
	 * @param array the one dim array object
	 * @param values the values
	 * @param parser the parser
	 * @param validator the validator
	 * @throws Exception on error
	 */
	private static void fillOneDimArray(Object array, String[] values, IAutoConfigParser<?> parser, IAutoConfigValidator<?> validator) throws Exception
	{
		for (int i = 0;i < values.length;++ i)
		{
			String valueString = PATTERN_ELEMENT_ESCAPES.matcher(values[i]).replaceAll("$1");
			Object value;
			if (parser.getClass() != NullParser.class)
			{
				value = parser.parse(valueString);
			}
			else
			{
				value = doValueOfConversion(valueString, array.getClass().getComponentType());
			}

			if (validator.getClass() != NullValidator.class)
			{
				validator.getClass().getMethod("validate", array.getClass().getComponentType()).invoke(validator, value);
			}

			Array.set(array, i, value);
		}
	}
	
	private static <T> void assignField(T instance, Field field, String valueString, IAutoConfigParser<?> parser, IAutoConfigValidator<?> validator) throws Exception
	{
		Object value;
		if (parser.getClass() != NullParser.class)
		{
			value = parser.parse(valueString);
		}
		else
		{
			value = doValueOfConversion(valueString, field.getType());
		}

		if (validator.getClass() != NullValidator.class)
		{
			validator.getClass().getMethod("validate", field.getType()).invoke(validator, value);
		}

		assignField(instance, field, value);
	}

	private static <T> void assignArrayField(T instance, Field array, int dimensions, Class<?> elementType, String valueString, IAutoConfigParser<?> parser, IAutoConfigValidator<?> validator) throws Exception
	{
		if (dimensions == 1)
		{
			if (!PATTERN_ARR_ONE_DIM.matcher(valueString).matches())
			{
				throw new Exception("Wrong format for one dimensional array!");
			}

			valueString = valueString.substring(1, valueString.length() - 1);
			String[] values = PATTERN_SPLIT_ARR_ELEMENTS.split(valueString);
			Object oneDimArray = Array.newInstance(elementType, values.length);
			fillOneDimArray(oneDimArray, values, parser, validator);
			assignField(instance, array, oneDimArray);
		}
		else
		{
			Matcher m = PATTERN_ARR_TWO_DIM.matcher(valueString);
			if (!m.find())
			{
				throw new Exception("Wrong format for two dimensional array!");
			}

			String group1 = m.group(1);
			valueString = group1.substring(1, group1.length() - 1);
			String[] oneDims = PATTERN_SPLIT_ONE_DIMS.split(valueString);
			Object twoDimArray = Array.newInstance(array.getType().getComponentType(), oneDims.length);
			for (int i = 0;i < oneDims.length;++ i)
			{
				String[] values = PATTERN_SPLIT_ARR_ELEMENTS.split(oneDims[i]);
				Object oneDimArray = Array.newInstance(elementType, values.length);
				fillOneDimArray(oneDimArray, values, parser, validator);
				Array.set(twoDimArray, i, oneDimArray);
			}
			assignField(instance, array, twoDimArray);
		}
	}
	
	private static <T> void load(Class<? extends T> loadClass, T instance, Context ctx) throws Exception
	{
		if (ctx == null)
		{
			ctx = new Context();
		}

		ctx.processAnnotations(loadClass.getAnnotations());
		try
		{
			final int modifiers = instance == null ? Modifier.STATIC : 0;
			final Field[] fields = loadClass.getDeclaredFields();
			for (final Field field : fields)
			{
				if ((field.getModifiers() & modifiers) != modifiers)
				{
					continue;
				}

				final AutoConfigObject objectAnnotation = field.getAnnotation(AutoConfigObject.class);
				final AutoConfigEntry entryAnnotation = field.getAnnotation(AutoConfigEntry.class);
				if (objectAnnotation == null && entryAnnotation == null)
				{
					continue;
				}
				if (objectAnnotation != null && entryAnnotation != null)
				{
					throw new IllegalStateException("AutoConfigObject & AutoConfigEntry on a single field! " + field.getName());
				}

				ctx.processAnnotations(field.getAnnotations());
				try
				{
					final Class<?> fieldType = field.getType();

					if (objectAnnotation != null)
					{
						Object value = fieldType.newInstance();
						load(fieldType, value, ctx);
						assignField(instance, field, value);
					}
					else if (entryAnnotation != null)
					{
						final String annotationId = entryAnnotation.id();
						final String id = annotationId.equals("") ? field.getName() : annotationId;
						final String valueString = entryAnnotation.hasDef() ? ctx.getValue(id, entryAnnotation.def()) : ctx.getValue(id);
						final Class<? extends IAutoConfigParser<?>> parserType = entryAnnotation.parser();
						final Class<? extends IAutoConfigValidator<?>> validatorType = entryAnnotation.validator();

						Class<?> parseType;
						int arrayDimensions = 0;
						if (fieldType.isArray())
						{
							parseType = fieldType;
							do
							{
								++ arrayDimensions;
								if (arrayDimensions > 2)
								{
									throw new Exception("Too many array dimensions! Upto 2 supported! " + field.getName());
								}
								parseType = parseType.getComponentType();
							}
							while (parseType.isArray());
						}
						else
						{
							parseType = fieldType;
						}

						if (parserType != NullParser.class && !parseType.isAssignableFrom(parserType.getMethod("parse", String.class).getReturnType()))
						{
							throw new Exception("Return type of parser mismatch!");
						}
						else if (validatorType != NullValidator.class)
						{
							validatorType.getMethod("validate", parseType);
						}

						if (arrayDimensions < 1)
						{
							assignField(instance, field, valueString, parserType.newInstance(), validatorType.newInstance());
						}
						else
						{
							assignArrayField(instance, field, arrayDimensions, parseType, valueString, parserType.newInstance(), validatorType.newInstance());
						}
					}
				}
				finally
				{
					ctx.clearLastAnnotationProcessing();
				}
			}
		}
		finally
		{
			ctx.clearLastAnnotationProcessing();
		}
	}
	
	public static <T> void load(Class<? extends T> loadClass, T instance) throws Exception
	{
		load(loadClass, instance, null);
	}

	public static <T> void load(Class<? extends T> loadClass) throws Exception
	{
		load(loadClass, null, null);
	}
}
