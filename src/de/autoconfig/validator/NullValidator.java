package de.autoconfig.validator;

public final class NullValidator implements IAutoConfigValidator<Void>
{
	@Override
	public void validate(Void value)
	{
		throw new UnsupportedOperationException();
	}

}
