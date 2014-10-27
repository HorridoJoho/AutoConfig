package de.autoconfig.exception;

public final class AutoConfigMissingEntryException extends Exception
{
	private static final long serialVersionUID = -3437600092892551691L;

	public AutoConfigMissingEntryException(String message)
	{
		super(message);
	}

	public AutoConfigMissingEntryException(Throwable cause)
	{
		super(cause);
	}

	public AutoConfigMissingEntryException(String message, Throwable cause) 
	{
		super(message, cause);
	}
}
