/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.meta;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.CompositeMap;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ClassDescriptor<T> implements Serializable {
   private static final long serialVersionUID = 1L;

   private static final Map<Class<?>, ClassDescriptor<?>> REGISTRY = new WeakHashMap<>();

   @SuppressWarnings("unchecked")
   public static <T> ClassDescriptor<T> of(final Class<T> type, final String name, final @Nullable String description,
         final @Nullable ClassDescriptor<?> parent) {
      Args.notNull("type", type);
      Args.notNull("name", name);
      Assert.isFalse(REGISTRY.containsKey(type), "A meta class for [" + type.getName() + "] exists already.");
      synchronized (type) {
         final ClassDescriptor<?> metaClass = REGISTRY.get(type);
         if (metaClass != null)
            return (ClassDescriptor<T>) metaClass;

         final var newMetaClass = new ClassDescriptor<>(type, name, description, parent);
         REGISTRY.put(type, newMetaClass);
         return newMetaClass;
      }
   }

   private final Class<T> type;
   @Nullable
   private final transient ClassDescriptor<?> parent;
   private final transient String name;
   @Nullable
   private final transient String description;

   private final transient Map<String, PropertyDescriptor<?>> properties = Maps.newLinkedHashMap();
   private final transient Map<String, PropertyDescriptor<?>> propertiesReadOnly = Collections.unmodifiableMap(properties);
   private final transient Map<String, PropertyDescriptor<?>> propertiesRecursivelyReadOnly;

   private ClassDescriptor(final Class<T> type, final String name, final @Nullable String description,
         final @Nullable ClassDescriptor<?> parent) {
      this.type = type;
      this.name = name;
      this.description = description;
      this.parent = parent;
      propertiesRecursivelyReadOnly = parent == null ? propertiesReadOnly : CompositeMap.of(propertiesReadOnly, parent.getProperties());
   }

   void addProperty(final PropertyDescriptor<?> prop) {
      Assert.isFalse(properties.containsKey(prop.getName()), "A meta property with name [" + prop.getName() + "] exists already for class ["
            + type.getName() + "]");
      properties.put(prop.getName(), prop);
   }

   @Nullable
   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }

   @Nullable
   public ClassDescriptor<?> getParent() {
      return parent;
   }

   public Map<String, PropertyDescriptor<?>> getProperties() {
      return propertiesReadOnly;
   }

   public Map<String, PropertyDescriptor<?>> getPropertiesRecursively() {
      return propertiesRecursivelyReadOnly;
   }

   public Class<T> getType() {
      return type;
   }

   private Object readResolve() throws ObjectStreamException {
      synchronized (type) {
         final ClassDescriptor<?> metaClass = REGISTRY.get(type);
         if (metaClass != null)
            return metaClass;
         throw new InvalidObjectException("MetaClass instance for type [" + type.getName() + "] not found in registry!");
      }
   }
}
