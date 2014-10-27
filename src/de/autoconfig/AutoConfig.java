package de.autoconfig;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.parser.IAutoConfigParser;
import de.autoconfig.parser.NullParser;
import de.autoconfig.source.IAutoConfigSource;
import de.autoconfig.source.IAutoConfigSourceLoader;
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

	private static Object doValueOfConversion(String strValue, Class<?> cls) throws Exception
	{
		if (cls == String.class)
			return strValue;

		if (cls.isPrimitive())
		{
			if (cls == byte.class)
				cls = Byte.class;
			else if (cls == short.class)
				cls = Short.class;
			else if (cls == int.class)
				cls = Integer.class;
			else if (cls == long.class)
				cls = Long.class;
			else if (cls == float.class)
				cls = Float.class;
			else if (cls == double.class)
				cls = Double.class;
			else if (cls == boolean.class)
				cls = Boolean.class;
			else if (cls == char.class)
				throw new Exception("Character type properties are not supported!");
			else
				throw new Exception("Missing primitive type check!");

			strValue = strValue.trim();
		}

		return cls.getMethod("valueOf", String.class).invoke(null, strValue);
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
		
		field.setAccessible(true);
		field.set(instance, value);
		field.setAccessible(false);
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
			array.setAccessible(true);
			array.set(instance, oneDimArray);
			array.setAccessible(false);
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
			array.setAccessible(true);
			array.set(instance, twoDimArray);
			array.setAccessible(false);
		}
	}
	
	public static <T> void load(Class<? extends T> loadClass, T instance) throws Exception
	{
		final int modifiers = instance == null ? Modifier.STATIC : 0;
		final HashMap<Class<? extends IAutoConfigSourceLoader>, IAutoConfigSourceLoader> loadersMap = new HashMap<>();
		final HashMap<String, IAutoConfigSource> sourcesMap = new HashMap<>();

		final Field[] fields = loadClass.getDeclaredFields();
		for (final Field field : fields)
		{
			if (modifiers > 0 && (field.getModifiers() & modifiers) != modifiers)
			{
				continue;
			}

			final AutoConfigEntry annotation = field.getAnnotation(AutoConfigEntry.class);
			if (annotation == null)
			{
				continue;
			}

			final Class<? extends IAutoConfigSourceLoader> loaderClass = annotation.loader();
			IAutoConfigSourceLoader loader = loadersMap.get(loaderClass);
			if (loader == null)
			{
				loader = loaderClass.getConstructor().newInstance();
				loadersMap.put(loaderClass, loader);
			}

			final String sourceString = annotation.source();
			final String mapSourceKey = loaderClass.getName() + sourceString;
			IAutoConfigSource source = sourcesMap.get(mapSourceKey);
			if (source == null)
			{
				source = loader.load(sourceString);
				sourcesMap.put(mapSourceKey, source);
			}

			final String identString = annotation.ident().equals("") ? field.getName() : annotation.ident();
			final Class<? extends IAutoConfigParser<?>> parserClass = annotation.parser();
			final Class<? extends IAutoConfigValidator<?>> validatorClass = annotation.validator();

			final String valueString = annotation.hasDefValue() ? source.getValue(identString, annotation.defValue()) : source.getValue(identString);

			Class<?> fieldClass = field.getType();
			Class<?> parseClass;
			int arrayDimensions = 0;
			if (fieldClass.isArray())
			{
				parseClass = fieldClass;
				do
				{
					++ arrayDimensions;
					if (arrayDimensions > 2)
					{
						throw new Exception("Too many array dimensions! Upto 2 supported! " + field.getName());
					}
					parseClass = parseClass.getComponentType();
				}
				while (parseClass.isArray());
			}
			else
			{
				parseClass = fieldClass;
			}

			if (parserClass != NullParser.class && !parseClass.isAssignableFrom(parserClass.getMethod("parse", String.class).getReturnType()))
			{
				throw new Exception("Return type of parser mismatch!");
			}
			else if (validatorClass != NullValidator.class)
			{
				validatorClass.getMethod("validate", parseClass);
			}

			if (arrayDimensions < 1)
			{
				assignField(instance, field, valueString, parserClass.newInstance(), validatorClass.newInstance());
			}
			else
			{
				assignArrayField(instance, field, arrayDimensions, parseClass, valueString, parserClass.newInstance(), validatorClass.newInstance());
			}
		}
	}

	public static <T> void load(Class<? extends T> loadClass) throws Exception
	{
		load(loadClass, null);
	}
}
