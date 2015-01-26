package de.autoconfig.test.config;

import de.autoconfig.annotation.AutoConfigObject;

public final class Config
{
	@AutoConfigObject
	public static Server Server;

	@AutoConfigObject
	public static FloodProtectors FloodProtector;
}
