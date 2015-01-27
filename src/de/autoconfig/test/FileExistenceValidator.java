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
package de.autoconfig.test;

import java.io.File;

import de.autoconfig.exception.AutoConfigValidationException;
import de.autoconfig.validator.IAutoConfigValidator;

/**
 * @author HorridoJoho
 */
public final class FileExistenceValidator implements IAutoConfigValidator<File>
{
	@Override
	public void validate(File value) throws AutoConfigValidationException
	{
		if (!value.exists())
		{
			throw new AutoConfigValidationException("Specified file " + value.toString() + " does not exists!");
		}
	}
}
