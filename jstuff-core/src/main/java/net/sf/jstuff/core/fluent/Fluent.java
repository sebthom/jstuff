/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.fluent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a method as applicable for fluent interface method-chaining.
 *
 * See <a href="https://en.wikipedia.org/wiki/Fluent_interface">https://en.wikipedia.org/wiki/Fluent_interface</a>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fluent {
}
