/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Identifiable<IdType> {
   /**
    * Default implementation that uses the object's class as id realm
    */
   class Default<IdType> implements Identifiable<IdType>, Serializable {
      private static final long serialVersionUID = 1L;

      @Nullable
      private IdType id;

      @Nullable
      @Override
      public IdType getId() {
         return id;
      }

      public void setId(final IdType id) {
         this.id = id;
      }

      @Override
      public Object getIdRealm() {
         return getClass();
      }

      @Override
      public String toString() {
         return ToStringBuilder.reflectionToString(this);
      }
   }

   @Nullable
   IdType getId();

   /**
    * @return an identifier for the realm/namespace in which the id is unique, e.g. the concrete class name.
    */
   Object getIdRealm();
}
