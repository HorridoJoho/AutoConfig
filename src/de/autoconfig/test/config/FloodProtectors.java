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

import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.annotation.AutoConfigLoader;
import de.autoconfig.annotation.AutoConfigObject;
import de.autoconfig.annotation.AutoConfigPrefix;
import de.autoconfig.loader.ResourceSourceLoader;
import de.autoconfig.test.PunishmentType;

@AutoConfigLoader(loader=ResourceSourceLoader.class, src="resource/config/FloodProtector.properties")
public final class FloodProtectors
{
	public static final class FloodProtector
	{
		@AutoConfigEntry
		public int Interval;
		@AutoConfigEntry
		public boolean LogFlooding;
		@AutoConfigEntry
		public int PunishmentLimit;
		@AutoConfigEntry
		public PunishmentType PunishmentType;
		@AutoConfigEntry
		public int PunishmentTime;
	}
	
	@AutoConfigPrefix("FloodProtectorUseItem")
	@AutoConfigObject
	public FloodProtector UseItem;

	@AutoConfigPrefix("FloodProtectorRollDice")
	@AutoConfigObject
	public FloodProtector RollDice;
}
