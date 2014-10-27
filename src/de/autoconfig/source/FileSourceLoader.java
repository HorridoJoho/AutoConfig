package de.autoconfig.source;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.autoconfig.exception.AutoConfigMissingEntryException;

public final class FileSourceLoader implements IAutoConfigSourceLoader
{
	private final class FileSource extends Properties implements IAutoConfigSource
	{
		private static final long serialVersionUID = 9111201865620567424L;

		private FileSource(String path) throws IOException
		{
			try (final InputStream is = new FileInputStream(path))
			{
				load(is);
			}
		}

		@Override
		public String getValue(String key) throws AutoConfigMissingEntryException
		{
			String value = getProperty(key);
			if (value == null)
			{
				throw new AutoConfigMissingEntryException(key);
			}
			return value;
		}

		@Override
		public String getValue(String key, String defValue)
		{
			return getProperty(key, defValue);
		}
	}

	@Override
	public IAutoConfigSource load(String path) throws Exception
	{
		return new FileSource(path);
	}
}
