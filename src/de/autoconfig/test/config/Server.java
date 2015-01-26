package de.autoconfig.test.config;

import de.autoconfig.annotation.AutoConfigEntry;
import de.autoconfig.annotation.AutoConfigLoader;
import de.autoconfig.loader.ResourceSourceLoader;

@AutoConfigLoader(loader=ResourceSourceLoader.class, src="resource/config/Server.properties")
public final class Server
{
	@AutoConfigEntry
	public int MaxPlayers;
}
