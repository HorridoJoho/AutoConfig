package de.autoconfig.test.staticconfig;

import java.io.File;

import org.junit.Test;

import de.autoconfig.AutoConfig;
import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.source.ResourceSourceLoader;
import de.autoconfig.test.FileExistenceValidator;
import de.autoconfig.test.FileParser;
import de.autoconfig.test.TestEnum;

public class StaticConfig
{
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="byteVal")
	public static byte BYTE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="shortVal")
	public static short SHORT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="intVal")
	public static int INT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="longVal")
	public static long LONG_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="floatVal")
	public static float FLOAT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="doubleVal")
	public static double DOUBLE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="booleanVal")
	public static boolean BOOLEAN_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringVal")
	public static String STRING_VALUE;
	
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringArray")
	public static String[] STRING_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringArray2")
	public static String[][] STRING_ARRAY_2;

	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileVal", hasDefValue=true, defValue=".", parser=FileParser.class, validator=FileExistenceValidator.class)
	public static File FILE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileArray", hasDefValue=true, defValue="[.;.]", parser=FileParser.class, validator=FileExistenceValidator.class)
	public static File[] FILE_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileArray2", hasDefValue=true, defValue="[[.;.][.;.]]", parser=FileParser.class, validator=FileExistenceValidator.class)
	public static File[][] FILE_ARRAY_2;
	
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnum")
	public static TestEnum SOME_ENUM;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnumArray")
	public static TestEnum[] SOME_ENUM_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnumArray2")
	public static TestEnum[][] SOME_ENUM_ARRAY_2;

	@Test
	public void test() throws Exception
	{
		AutoConfig.load(StaticConfig.class);
	}
	
	public static void main(String[] args) throws Exception
	{
		new StaticConfig().test();
	}
}
