package de.autoconfig.validator;

import de.autoconfig.exception.AutoConfigValidationException;

public interface IAutoConfigValidator<T>
{
	void validate(T value) throws AutoConfigValidationException;
}
