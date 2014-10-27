package de.autoconfig.parser;

public final class NullParser implements IAutoConfigParser<Void>
{
	@Override
	public Void parse(String stringValue)
	{
		throw new UnsupportedOperationException();
	}
}
