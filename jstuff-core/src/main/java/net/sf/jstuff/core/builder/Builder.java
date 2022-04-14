/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Builder<TARGET_TYPE> {
   TARGET_TYPE build();

   /**
    * To specify the default behaviour for all properties add this annotation to the builder interface,
    * otherwise to the respective interface methods representing the properties.
    */
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.METHOD, ElementType.TYPE})
   public @interface Property {

      /**
       * specifies if the property can have a null value
       */
      boolean nullable() default false;

      /**
       * specifies if the property must be set
       */
      boolean required() default false;
   }

}
