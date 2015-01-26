package de.autoconfig.loader;

import de.autoconfig.source.IAutoConfigSource;

public final class NullLoader implements IAutoConfigSourceLoader
{
	@Override
	public IAutoConfigSource load(String path) throws Exception
	{
		throw new UnsupportedOperationException();
	}

}
