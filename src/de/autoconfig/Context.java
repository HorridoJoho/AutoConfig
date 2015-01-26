/*
 * This file is part of AutoConfig: https://github.com/HorridoJoho/AutoConfig
 * Copyright (C) 2015  Christian Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.autoconfig;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import de.autoconfig.annotation.AutoConfigLoader;
import de.autoconfig.annotation.AutoConfigPostfix;
import de.autoconfig.annotation.AutoConfigPrefix;
import de.autoconfig.annotation.AutoConfigSource;
import de.autoconfig.loader.IAutoConfigSourceLoader;
import de.autoconfig.source.IAutoConfigSource;

final class Context
{
	private LinkedList<SourceLoaderStackPair> _sourceLoaderStack;
	private LinkedList<String> _prefixStack;
	private LinkedList<String> _postfixStack;
	private LinkedList<StackCountInfo> _stackCountInfos;
	private Map<Class<? extends IAutoConfigSourceLoader>, IAutoConfigSourceLoader> _sourceLoaderInstances;
	private Map<String, IAutoConfigSource> _sourceInstances;
	
	Context()
	{
		_sourceLoaderStack = new LinkedList<>();
		_prefixStack = new LinkedList<>();
		_postfixStack = new LinkedList<>();
		_stackCountInfos = new LinkedList<>();
		_sourceLoaderInstances = new HashMap<>();
		_sourceInstances = new HashMap<>();
	}
	
	private void requireNonEmptySourceLoaderStack()
	{
		if (_sourceLoaderStack.isEmpty())
		{
			throw new IllegalStateException("empty loader stack");
		}
	}
	
	private IAutoConfigSourceLoader getSourceLoader(Class<? extends IAutoConfigSourceLoader> sourceLoaderType) throws Exception
	{
		Objects.requireNonNull(sourceLoaderType);

		IAutoConfigSourceLoader sourceLoader = _sourceLoaderInstances.get(sourceLoaderType);
		if (sourceLoader == null)
		{
			sourceLoader = sourceLoaderType.newInstance();
			_sourceLoaderInstances.put(sourceLoaderType, sourceLoader);
		}
		return sourceLoader;
	}

	private IAutoConfigSource getSource() throws Exception
	{
		requireNonEmptySourceLoaderStack();

		SourceLoaderStackPair pair = _sourceLoaderStack.getLast();
		String sourceInstanceKey = pair.sourceLoaderType.getName() + pair.sourceString;

		IAutoConfigSource source = _sourceInstances.get(sourceInstanceKey);
		if (source == null)
		{
			IAutoConfigSourceLoader sourceLoader = getSourceLoader(pair.sourceLoaderType);
			source = sourceLoader.load(pair.sourceString);
			_sourceInstances.put(sourceInstanceKey, source);
		}

		return source;
	}
	
	void processAnnotations(Annotation[] annotations)
	{
		int sourceLoaderCount = 0;
		int prefixCount = 0;
		int postfixCount = 0;

		try
		{
			for (final Annotation annotation : annotations)
			{
				if (annotation instanceof AutoConfigLoader)
				{
					AutoConfigLoader annotation2 = (AutoConfigLoader)annotation;
					pushSourceLoaderStack(annotation2.loader(), annotation2.src());
					++ sourceLoaderCount;
				}
				else if (annotation instanceof AutoConfigSource)
				{
					AutoConfigSource annotation2 = (AutoConfigSource)annotation;
					pushSourceLoaderStack(annotation2.value());
					++ sourceLoaderCount;
				}
				else if (annotation instanceof AutoConfigPrefix)
				{
					AutoConfigPrefix annotation2 = (AutoConfigPrefix)annotation;
					pushPrefixStack(annotation2.value());
					++ prefixCount;
				}
				else if (annotation instanceof AutoConfigPostfix)
				{
					AutoConfigPostfix annotation2 = (AutoConfigPostfix)annotation;
					pushPostfixStack(annotation2.value());
					++ postfixCount;
				}
			}
		}
		finally
		{
			_stackCountInfos.addLast(new StackCountInfo(sourceLoaderCount, prefixCount, postfixCount));
		}
	}
	
	public void clearLastAnnotationProcessing()
	{
		if (_stackCountInfos.isEmpty())
		{
			throw new IllegalStateException("empty stack count infos");
		}

		StackCountInfo info = _stackCountInfos.removeLast();
		int i;
		for (i = 0;i < info.sourceLoaderCount;++ i)
		{ 
			popSourceLoaderStack();
		}
		for (i = 0;i < info.prefixCount;++ i)
		{
			popPrefixStack();
		}
		for (i = 0;i < info.postfixCount;++ i)
		{
			popPostfixStack();
		}
	}

	private void pushSourceLoaderStack(Class<? extends IAutoConfigSourceLoader> sourceLoaderType, String sourceString)
	{
		Objects.requireNonNull(sourceLoaderType);
		Util.requireNonEmptyString(sourceString, "empty source string");
		_sourceLoaderStack.addLast(new SourceLoaderStackPair(sourceLoaderType, sourceString));
	}
	
	private void pushSourceLoaderStack(String sourceString)
	{
		Util.requireNonEmptyString(sourceString, "empty source string");
		requireNonEmptySourceLoaderStack();
		_sourceLoaderStack.addLast(new SourceLoaderStackPair(_sourceLoaderStack.getLast().sourceLoaderType, sourceString));
	}
	
	private void popSourceLoaderStack()
	{
		requireNonEmptySourceLoaderStack();
		_sourceLoaderStack.removeLast();
	}
	
	private void pushPrefixStack(String prefix)
	{
		Objects.requireNonNull(prefix);
		_prefixStack.addLast(prefix);
	}
	
	private void popPrefixStack()
	{
		if (_prefixStack.isEmpty())
		{
			throw new IllegalStateException("empty prefix stack");
		}
		_prefixStack.removeLast();
	}
	
	private void pushPostfixStack(String postfix)
	{
		Objects.requireNonNull(postfix);
		_postfixStack.addLast(postfix);
	}
	
	private void popPostfixStack()
	{
		if (_postfixStack.isEmpty())
		{
			throw new IllegalStateException("empty postfix stack");
		}
		_postfixStack.removeLast();
	}
	
	String getValue(String key) throws Exception
	{
		if (!_prefixStack.isEmpty())
		{
			key = _prefixStack.getLast() + key;
		}
		if (!_postfixStack.isEmpty())
		{
			key = _postfixStack.getLast() + key;
		}

		return getSource().getValue(key);
	}

	String getValue(String key, String def) throws Exception
	{
		if (!_prefixStack.isEmpty())
		{
			key = _prefixStack.getLast() + key;
		}
		if (!_postfixStack.isEmpty())
		{
			key = _postfixStack.getLast() + key;
		}

		return getSource().getValue(key, def);
	}
}
