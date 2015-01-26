package de.autoconfig.loader;

import de.autoconfig.source.IAutoConfigSource;

public interface IAutoConfigSourceLoader
{
	IAutoConfigSource load(String path) throws Exception;
}
