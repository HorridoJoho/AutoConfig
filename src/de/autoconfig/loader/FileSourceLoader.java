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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.autoconfig.exception.AutoConfigMissingEntryException;
import de.autoconfig.source.IAutoConfigSource;

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
