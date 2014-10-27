package de.autoconfig.source;

public interface IAutoConfigSourceLoader
{
	IAutoConfigSource load(String path) throws Exception;
}
