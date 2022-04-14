/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface REST_PATCH { // CHECKSTYLE:IGNORE (AbbreviationAsWordInName|TypeName)
   /**
    * POST resource id for clients not supporting PATCH
    */
   String fallback() default "";

   /**
    * resource name
    */
   String value();
}
