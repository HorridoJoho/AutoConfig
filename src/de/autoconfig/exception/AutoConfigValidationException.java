package de.autoconfig.exception;

public final class AutoConfigValidationException extends Exception
{
	private static final long serialVersionUID = -1287762397986293230L;

	public AutoConfigValidationException(String message)
	{
		super(message);
	}

	public AutoConfigValidationException(Throwable cause)
	{
		super(cause);
	}

	public AutoConfigValidationException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
