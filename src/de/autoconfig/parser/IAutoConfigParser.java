package de.autoconfig.parser;

public interface IAutoConfigParser<T>
{
	T parse(String valueString);
}
