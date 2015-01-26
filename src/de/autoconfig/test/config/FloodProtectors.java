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
