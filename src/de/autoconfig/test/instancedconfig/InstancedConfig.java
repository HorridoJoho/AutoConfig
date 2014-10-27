package de.autoconfig.test.instancedconfig;

import java.io.File;

import org.junit.Test;

import de.autoconfig.AutoConfig;
import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.source.ResourceSourceLoader;
import de.autoconfig.test.FileExistenceValidator;
import de.autoconfig.test.FileParser;
import de.autoconfig.test.TestEnum;
import de.autoconfig.test.staticconfig.StaticConfig;

public class InstancedConfig
{
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="byteVal")
	public byte BYTE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="shortVal")
	public short SHORT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="intVal")
	public int INT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="longVal")
	public long LONG_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="floatVal")
	public float FLOAT_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="doubleVal")
	public double DOUBLE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="booleanVal")
	public boolean BOOLEAN_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringVal")
	public String STRING_VALUE;
	
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringArray")
	public String[] STRING_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="stringArray2")
	public String[][] STRING_ARRAY_2;

	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileVal", hasDefValue=true, defValue=".", parser=FileParser.class, validator=FileExistenceValidator.class)
	public File FILE_VALUE;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileArray", hasDefValue=true, defValue="[.;.]", parser=FileParser.class, validator=FileExistenceValidator.class)
	public File[] FILE_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="fileArray2", hasDefValue=true, defValue="[[.;.][.;.]]", parser=FileParser.class, validator=FileExistenceValidator.class)
	public File[][] FILE_ARRAY_2;
	
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnum")
	public TestEnum SOME_ENUM;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnumArray")
	public TestEnum[] SOME_ENUM_ARRAY;
	@AutoConfigEntry(loader=ResourceSourceLoader.class, source="config.properties", ident="someEnumArray2")
	public TestEnum[][] SOME_ENUM_ARRAY_2;

	@Test
	public void test() throws Exception
	{
		AutoConfig.load(InstancedConfig.class, this);
	}

	public static void main(String[] args) throws Exception
	{
		new StaticConfig().test();
	}
}
