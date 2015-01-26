package de.autoconfig.loader;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.autoconfig.exception.AutoConfigMissingEntryException;
import de.autoconfig.source.IAutoConfigSource;

public final class ResourceBundleSourceLoader implements IAutoConfigSourceLoader
{
	private final class ResourceBundleSource implements IAutoConfigSource
	{
		private final ResourceBundle _bundle;

		private ResourceBundleSource(String baseName)
		{
			_bundle = ResourceBundle.getBundle(baseName);
		}

		@Override
		public String getValue(String key) throws AutoConfigMissingEntryException
		{
			try
			{
				return _bundle.getString(key);
			}
			catch (MissingResourceException mre)
			{
				throw new AutoConfigMissingEntryException(key, mre);
			}
		}

		@Override
		public String getValue(String key, String defValue)
		{
			try
			{
				return _bundle.getString(key);
			}
			catch (MissingResourceException mre)
			{
				return defValue;
			}
		}
	}

	@Override
	public IAutoConfigSource load(String path)
	{
		return new ResourceBundleSource(path);
	}
}
