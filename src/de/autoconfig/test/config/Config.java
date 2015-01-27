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
package de.autoconfig.test.config;

import de.autoconfig.annotation.AutoConfigLoader;
import de.autoconfig.annotation.AutoConfigObject;
import de.autoconfig.annotation.AutoConfigSource;
import de.autoconfig.loader.ResourceSourceLoader;

/**
 * @author HorridoJoho
 */
@AutoConfigLoader(ResourceSourceLoader.class)
public final class Config
{
	@AutoConfigObject
	@AutoConfigSource("resource/config/Server.properties")
	public static Server Server;

	@AutoConfigObject
	@AutoConfigSource("resource/config/FloodProtector.properties")
	public static FloodProtectors FloodProtector;
}
