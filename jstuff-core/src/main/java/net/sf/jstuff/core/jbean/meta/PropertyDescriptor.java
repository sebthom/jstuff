/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.jbean.meta;

import static java.util.Collections.*;

import java.io.Serializable;
import java.util.Map;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class PropertyDescriptor<P> implements Serializable {
   private static final long serialVersionUID = 1L;

   public static <P> PropertyDescriptor<P> create( // CHECKSTYLE:IGNORE ParameterNumber
      final ClassDescriptor<?> metaClass, //
      final String name, //
      final Class<P> type, //
      final int lowerBound, //
      final int upperBound, //
      final boolean ordered, //
      final boolean unique, //
      final boolean container, //
      final String description, //
      final Map<String, ? extends Serializable> properties //
   ) {
      synchronized (metaClass) {
         final PropertyDescriptor<P> p = new PropertyDescriptor<>();
         p.metaClass = metaClass;
         p.name = name;
         p.lowerBound = lowerBound;
         p.upperBound = upperBound;
         p.type = type;
         p.ordered = ordered;
         p.unique = unique;
         p.container = container;
         p.description = description == null ? "" : description;
         p.properties = Maps.newHashMap(properties);
         p.propertiesReadOnly = unmodifiableMap(p.properties);
         metaClass.addProperty(p);
         return p;
      }
   }

   private ClassDescriptor<?> metaClass;

   private String name;
   private transient String description;
   private transient int lowerBound;
   private transient int upperBound;
   private transient Class<P> type;
   private transient boolean container;
   private transient boolean ordered;
   private transient boolean unique;
   private transient boolean writable;
   private transient Map<String, ? extends Serializable> properties;
   private transient Map<String, ? extends Serializable> propertiesReadOnly;

   private PropertyDescriptor() {
   }

   public String getDescription() {
      return description;
   }

   public int getLowerBound() {
      return lowerBound;
   }

   @SuppressWarnings("unchecked")
   public <T> ClassDescriptor<T> getMetaClass() {
      return (ClassDescriptor<T>) metaClass;
   }

   public String getName() {
      return name;
   }

   public Map<String, ? extends Serializable> getProperties() {
      return propertiesReadOnly;
   }

   public Class<P> getType() {
      return type;
   }

   public int getUpperBound() {
      return upperBound;
   }

   public boolean isContainer() {
      return container;
   }

   public boolean isMany() {
      return upperBound < 0 || upperBound > 1;
   }

   public boolean isOrdered() {
      return ordered;
   }

   public boolean isRequired() {
      return lowerBound > 0;
   }

   public boolean isScalarType() {
      return Types.isScalar(type);
   }

   public boolean isUnique() {
      return unique;
   }

   public boolean isWritable() {
      return writable;
   }

   private Object readResolve() {
      synchronized (metaClass.getType()) {
         return metaClass.getProperties().get(name);
      }
   }

   public void setContainer(final boolean container) {
      this.container = container;
   }

   @Override
   public String toString() {
      return "PropertyDescriptor [name=" + name + ", description=" + description + ", lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", type="
         + type + ", container=" + container + ", ordered=" + ordered + ", unique=" + unique + ", writable=" + writable + "]";
   }
}
