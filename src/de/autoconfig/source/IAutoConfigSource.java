package de.autoconfig.source;

import de.autoconfig.exception.AutoConfigMissingEntryException;

public interface IAutoConfigSource
{
	String getValue(String key) throws AutoConfigMissingEntryException;
	String getValue(String key, String def);
}
