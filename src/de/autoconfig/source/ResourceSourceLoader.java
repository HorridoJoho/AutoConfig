package de.autoconfig.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.autoconfig.exception.AutoConfigMissingEntryException;

public final class ResourceSourceLoader implements IAutoConfigSourceLoader
{
	private final class ResourceSource extends Properties implements IAutoConfigSource
	{
		private static final long serialVersionUID = 9111201865620567424L;

		private ResourceSource(String path) throws IOException
		{
			try (final InputStream is = getClass().getResourceAsStream("/" + path))
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
		return new ResourceSource(path);
	}
}
