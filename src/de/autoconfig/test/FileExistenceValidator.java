package de.autoconfig.test;

import java.io.File;

import de.autoconfig.exception.AutoConfigValidationException;
import de.autoconfig.validator.IAutoConfigValidator;

public final class FileExistenceValidator implements IAutoConfigValidator<File>
{
	@Override
	public void validate(File value) throws AutoConfigValidationException
	{
		if (!value.exists())
		{
			throw new AutoConfigValidationException("Specified file " + value.toString() + " does not exists!");
		}
	}
}
