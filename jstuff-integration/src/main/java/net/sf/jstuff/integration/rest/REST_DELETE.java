/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
public @interface REST_DELETE { // CHECKSTYLE:IGNORE (AbbreviationAsWordInName|TypeName)
   /**
    * POST resource id for clients not supporting DELETE
    */
   String fallback() default "";

   /**
    * resource name
    */
   String value();
}
