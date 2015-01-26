/*
 * This file is part of AutoConfig: https://github.com/HorridoJoho/AutoConfig
 * Copyright (C) 2015  Christian Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
