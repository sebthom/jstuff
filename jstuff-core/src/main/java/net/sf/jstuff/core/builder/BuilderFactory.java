/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.ref.MutableRef;
import net.sf.jstuff.core.reflection.Annotations;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.reflection.visitor.DefaultClassVisitor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderFactory<TARGET_CLASS, BLDR_IFACE extends Builder<? extends TARGET_CLASS>> {

   private static final class BuilderImpl implements InvocationHandler {

      private final Class<?> builderInterface;
      private final Class<?> targetClass;
      private final Object[] constructorArgs;

      private Builder.Property propertyDefaults;
      private final Map<String, Builder.@Nullable Property> propertyConfig = new HashMap<>(2);
      private final List<Method> onPostBuilds = new ArrayList<>(2);

      /**
       * ordered list of the values of all wither/setter invocations on the builder instance
       */
      private final List<Tuple2<String, @Nullable Object[]>> properties = new ArrayList<>();

      BuilderImpl(final Class<?> builderInterface, final Class<?> targetClass, final Object... constructorArgs) {
         this.builderInterface = builderInterface;
         this.targetClass = targetClass;
         this.constructorArgs = constructorArgs;

         // collecting annotation information on builder interface
         final MutableRef<Builder.@Nullable Property> propertyDefaultsRef = MutableRef.create();
         Types.visit(builderInterface, new DefaultClassVisitor() {
            @Override
            public boolean isVisitingFields(final Class<?> clazz) {
               return false;
            }

            @Override
            public boolean isVisitingMethod(final Method method) {
               if (Methods.isStatic(method) || method.getParameterTypes().length != 1)
                  return false;

               final Builder.Property annoOverride = propertyConfig.get(method.getName());
               if (annoOverride == null) {
                  propertyConfig.put(method.getName(), method.getAnnotation(Builder.Property.class));
               }
               return false;
            }

            @Override
            public boolean visit(final Class<?> clazz) {
               if (propertyDefaultsRef.isNull()) {
                  propertyDefaultsRef.set(clazz.getAnnotation(Builder.Property.class));
               }
               return true;
            }
         });

         final var pd = propertyDefaultsRef.get();
         propertyDefaults = pd == null ? Annotations.getDefaults(Builder.Property.class) : pd;

         // collecting @OnPostBuild methods on target class
         final Set<String> overridablePostBuildMethodNames = new HashSet<>(2);
         Types.visit(targetClass, new DefaultClassVisitor() {
            @Override
            public boolean isVisitingFields(final Class<?> clazz) {
               return false;
            }

            @Override
            public boolean isVisitingInterfaces(final Class<?> clazz) {
               return false;
            }

            @Override
            public boolean isVisitingMethod(final Method method) {
               if (!Methods.isAbstract(method) && !Methods.isStatic(method) && method.isAnnotationPresent(OnPostBuild.class)) {
                  if (method.getParameterTypes().length > 0)
                     throw new IllegalStateException("@OnPostBuild method [" + method + "] must not declare parameters!");
                  if (Methods.isPrivate(method)) {
                     onPostBuilds.add(method);
                  } else {
                     final String mName = method.getName();
                     if (!overridablePostBuildMethodNames.contains(mName)) {
                        onPostBuilds.add(method);
                        overridablePostBuildMethodNames.add(mName);
                     }
                  }
               }
               return false;
            }
         });
      }

      protected Object buildTarget() throws Throwable { // CHECKSTYLE:IGNORE .*
         // creating target instance
         final Object target = Types.newInstance(targetClass, constructorArgs);

         // setting properties
         for (final Tuple2<String, @Nullable Object[]> property : properties) {
            String propName = property.get1();
            // remove "with" prefix from withSomeProperty(...) named properties
            if (propName.length() > 4 && propName.startsWith("with")) {
               propName = Strings.lowerCaseFirstChar(propName.substring(4));
            }
            final @Nullable Object[] propArgs = property.get2();
            final String setterName = "set" + Strings.upperCaseFirstChar(propName);
            final Method setterMethod = Methods.findAnyCompatible(targetClass, setterName, propArgs);
            // if no setter found then directly try to set the field
            if (setterMethod == null) {
               if (propArgs.length != 1)
                  throw new IllegalStateException("Method [" + targetClass.getName() + "#" + setterName + "()] not found.");
               Types.writePropertyIgnoringFinal(target, propName, propArgs[0]);
            } else {
               Methods.invoke(target, setterMethod, propArgs);
            }
         }

         // validate @Property constraints
         if (propertyConfig.size() > 0) {
            for (final var prop : propertyConfig.entrySet()) {
               final String propName = prop.getKey();
               final Builder.Property propConfig = prop.getValue();

               boolean wasPropertySet = false;
               for (final Tuple2<String, @Nullable Object[]> property : properties) {
                  if (propName.equals(property.get1())) {
                     final boolean isNullable = propConfig == null ? propertyDefaults.nullable() : propConfig.nullable();
                     if (!isNullable && property.get2()[0] == null)
                        throw new IllegalArgumentException(builderInterface.getSimpleName() + "." + propName
                              + "(...) must not be set to null.");
                     wasPropertySet = true;
                  }
               }
               if (!wasPropertySet) {
                  final boolean isRequired = propConfig == null ? propertyDefaults.required() : propConfig.required();
                  if (isRequired)
                     throw new IllegalStateException("Setting " + builderInterface.getSimpleName() + "." + propName + "(...) is required.");
               }
            }
         }

         // invoke @OnPostBuild methods of the newly instantiated object
         for (int i = onPostBuilds.size() - 1; i >= 0; i--) {
            try {
               Methods.invoke(target, onPostBuilds.get(i), ArrayUtils.EMPTY_OBJECT_ARRAY);
            } catch (final InvokingMethodFailedException ex) {
               var cause = ex.getCause();
               if (cause instanceof InvocationTargetException) {
                  cause = ex.getCause();
                  if (cause != null)
                     throw cause;
               }
               throw ex;
            }
         }
         return target;
      }

      /**
       * handles invocation of withXYZ on builder interface
       */
      @Override
      public @Nullable Object invoke(final Object proxy, final Method method, final @Nullable Object @Nullable [] args) throws Throwable {
         final boolean isBuildMethod = "build".equals(method.getName()) //
               && method.getParameterTypes().length == 0 //
               && method.getReturnType().isAssignableFrom(targetClass);

         if (isBuildMethod)
            return buildTarget();

         if ("toString".equals(method.getName()) && method.getParameterTypes().length == 0)
            return builderInterface.getName() + "@" + hashCode();

         // collect values from setter invocations
         if (method.getReturnType().isAssignableFrom(builderInterface)) {
            properties.add(new Tuple2<>(method.getName(), args == null ? new @Nullable Object[0] : args));
            return proxy;
         }
         throw new UnsupportedOperationException(method.toString());
      }

   }

   /**
    * @param targetClass if <code>null</code> the builder factory tries to extract the generic argument type information from the
    *           builderInterface class
    */
   public static <TARGET_CLASS, BLDR_IFACE extends Builder<? extends TARGET_CLASS>> BuilderFactory<TARGET_CLASS, BLDR_IFACE> //
         of(final Class<BLDR_IFACE> builderInterface, final Class<TARGET_CLASS> targetClass, final Object... constructorArgs) {
      return new BuilderFactory<>(builderInterface, targetClass, constructorArgs);
   }

   public static <TARGET_CLASS, BLDR_IFACE extends Builder<? extends TARGET_CLASS>> BuilderFactory<TARGET_CLASS, BLDR_IFACE> //
         of(final Class<BLDR_IFACE> builderInterface, final Object... constructorArgs) {
      return new BuilderFactory<>(builderInterface, null, constructorArgs);
   }

   private final Class<BLDR_IFACE> builderInterface;
   private final Class<TARGET_CLASS> targetClass;

   private final Object[] constructorArgs;

   @SuppressWarnings("unchecked")
   protected BuilderFactory(final Class<BLDR_IFACE> builderInterface, @Nullable Class<TARGET_CLASS> targetClass,
         final Object... constructorArgs) {
      Args.notNull("builderInterface", builderInterface);

      if (!builderInterface.isInterface())
         throw new IllegalArgumentException("[builderInterface] '" + builderInterface.getName() + "' is not an interface!");

      this.builderInterface = builderInterface;

      if (targetClass == null) {
         targetClass = (Class<TARGET_CLASS>) Types.findGenericTypeArguments(builderInterface, Builder.class)[0];
      }

      this.targetClass = Args.notNull("targetClass", targetClass);

      if (this.targetClass.isInterface())
         throw new IllegalArgumentException("[targetClass] '" + this.targetClass.getName() + "' is an interface.");

      if (Types.isAbstract(this.targetClass))
         throw new IllegalArgumentException("[targetClass] '" + this.targetClass.getName() + "' is abstract.");

      this.constructorArgs = constructorArgs;
   }

   public BLDR_IFACE create() {
      return Proxies.create(new BuilderImpl(builderInterface, targetClass, constructorArgs), builderInterface);
   }
}
