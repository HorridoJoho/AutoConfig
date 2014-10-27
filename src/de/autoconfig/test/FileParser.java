package de.autoconfig.test;

import java.io.File;

import de.autoconfig.parser.IAutoConfigParser;

public final class FileParser implements IAutoConfigParser<File>
{
	@Override
	public File parse(String stringValue)
	{
		return new File(stringValue);
	}
}