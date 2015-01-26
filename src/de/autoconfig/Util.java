package de.autoconfig;

import java.util.Objects;

public final class Util
{
	public static void requireNonEmptyString(String str, String msg)
	{
		Objects.requireNonNull(str);
		if (str.isEmpty())
		{
			throw new IllegalArgumentException(msg);
		}
	}
}
