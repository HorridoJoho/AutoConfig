package de.autoconfig;

import java.util.Objects;

import de.autoconfig.loader.IAutoConfigSourceLoader;

final class SourceLoaderStackPair
{
	Class<? extends IAutoConfigSourceLoader> sourceLoaderType;
	String sourceString;
	
	SourceLoaderStackPair(Class<? extends IAutoConfigSourceLoader> sourceLoaderType, String sourceString)
	{
		Objects.requireNonNull(sourceLoaderType);
		this.sourceLoaderType = sourceLoaderType;
		this.sourceString = sourceString;
	}
}
