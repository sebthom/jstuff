/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.meta;

import static java.util.Collections.unmodifiableMap;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
         final @Nullable String description, //
         final Map<String, ? extends Serializable> properties //
   ) {
      synchronized (metaClass) {
         final var p = new PropertyDescriptor<>(metaClass, //
            name, //
            type, //
            lowerBound, //
            upperBound, //
            ordered, //
            unique, //
            container, //
            Strings.emptyIfNull(description), //
            properties);
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

   private PropertyDescriptor(final ClassDescriptor<?> metaClass, // CHECKSTYLE:IGNORE ParameterNumber
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
      this.metaClass = metaClass;
      this.name = name;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.type = type;
      this.ordered = ordered;
      this.unique = unique;
      this.container = container;
      this.description = description;
      this.properties = unmodifiableMap(Maps.newHashMap(properties));
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
      return properties;
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
         return asNonNullUnsafe(metaClass.getProperties().get(name));
      }
   }

   public void setContainer(final boolean container) {
      this.container = container;
   }

   @Override
   public String toString() {
      return "PropertyDescriptor [name=" + name + ", description=" + description + ", lowerBound=" + lowerBound + ", upperBound="
            + upperBound + ", type=" + type + ", container=" + container + ", ordered=" + ordered + ", unique=" + unique + ", writable="
            + writable + "]";
   }
}
