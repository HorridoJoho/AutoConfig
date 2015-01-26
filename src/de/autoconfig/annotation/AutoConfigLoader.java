package de.autoconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.autoconfig.loader.IAutoConfigSourceLoader;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface AutoConfigLoader
{
	Class<? extends IAutoConfigSourceLoader> loader();
	String src();
}
