package de.autoconfig.test;

import de.autoconfig.AutoConfig;
import de.autoconfig.test.config.Config;

public final class Main
{
	public static void main(String[] args) throws Exception
	{
		AutoConfig.load(Config.class);
		return;
	}
}
