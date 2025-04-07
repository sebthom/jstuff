/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Identifiable<ID> {
   /**
    * Default implementation that uses the object's class as id realm
    */
   class Default<ID> implements Identifiable<ID>, Serializable {
      private static final long serialVersionUID = 1L;

      private ID id;

      public Default(final ID id) {
         this.id = id;
      }

      @Override
      public ID getId() {
         return id;
      }

      public void setId(final @NonNull ID id) {
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

   ID getId();

   /**
    * @return an identifier for the realm/namespace in which the id is unique, e.g. the concrete class name.
    */
   Object getIdRealm();
}
