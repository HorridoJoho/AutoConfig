package de.autoconfig;

final class StackCountInfo
{
	public final int sourceLoaderCount;
	public final int prefixCount;
	public final int postfixCount;
	
	StackCountInfo(int sourceLoaderCount, int prefixCount, int postfixCount)
	{
		this.sourceLoaderCount = sourceLoaderCount;
		this.prefixCount = prefixCount;
		this.postfixCount = postfixCount;
	}
}
