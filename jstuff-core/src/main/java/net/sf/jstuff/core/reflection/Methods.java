/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Methods extends Members {

   @FunctionalInterface
   public interface GetterAccessor<BEAN_TYPE, PROPERTY_TYPE> {
      PROPERTY_TYPE invoke(BEAN_TYPE bean);
   }

   @FunctionalInterface
   public interface SetterAccessor<BEAN_TYPE, PROPERTY_TYPE> {
      void invoke(BEAN_TYPE bean, PROPERTY_TYPE propertyValue);
   }

   private static final Logger LOG = Logger.create();

   @SuppressWarnings("unchecked")
   public static <O, P> GetterAccessor<O, P> createPublicGetterAccessor(final Class<O> beanClass, final String propertyName, final Class<P> propertyType)
      throws ReflectionException {
      try {
         return createPublicMethodAccessor(GetterAccessor.class, beanClass, "is" + Strings.capitalize(propertyName), propertyType);
      } catch (final Exception ex) {
         return createPublicMethodAccessor(GetterAccessor.class, beanClass, "get" + Strings.capitalize(propertyName), propertyType);
      }
   }

   /**
    * @param accessor FunctionalInterface where the first parameter is the targetObject followed by the parameters for the target method
    */
   public static <ACCESSOR> ACCESSOR createPublicMethodAccessor(final Class<ACCESSOR> accessor, final Class<?> targetClass, final String methodName) {
      Args.notNull("accessor", accessor);

      if (!accessor.isAnnotationPresent(FunctionalInterface.class))
         throw new IllegalArgumentException("[accessor] must be an interface annotated with @java.lang.FunctionalInterface!");

      final Method delegatingMethod = Arrays.stream(accessor.getMethods()).filter(Members::isAbstract).findFirst().get();

      return createPublicMethodAccessor(accessor, targetClass, methodName, delegatingMethod.getReturnType(), ArrayUtils.remove(delegatingMethod
         .getParameterTypes(), 0));
   }

   /**
    * @param accessor FunctionalInterface where the first parameter is the targetObject followed by the parameters for the target method
    */
   public static <ACCESSOR> ACCESSOR createPublicMethodAccessor(final Class<ACCESSOR> accessor, final Class<?> targetClass, final String methodName,
      Class<?> methodReturnType, final Class<?>... methodParameterTypes) {
      Args.notNull("accessor", accessor);
      Args.notNull("targetClass", targetClass);
      Args.notNull("methodName", methodName);

      if (!accessor.isAnnotationPresent(FunctionalInterface.class))
         throw new IllegalArgumentException("[accessor] must be an interface annotated with @java.lang.FunctionalInterface!");

      if (methodReturnType == null) {
         methodReturnType = void.class;
      }

      try {
         final Method delegatingMethod = Arrays.stream(accessor.getMethods()).filter(Members::isAbstract).findFirst().get();

         final MethodHandles.Lookup lookup = MethodHandles.lookup();
         final CallSite site = LambdaMetafactory.metafactory(lookup, //
            delegatingMethod.getName(), //
            MethodType.methodType(accessor), //
            MethodType.methodType(delegatingMethod.getReturnType(), delegatingMethod.getParameterTypes()), //
            lookup.findVirtual(targetClass, methodName, MethodType.methodType(methodReturnType, methodParameterTypes)), //
            MethodType.methodType(methodReturnType, ArrayUtils.insert(0, methodParameterTypes, targetClass))); //
         return (ACCESSOR) site.getTarget().invoke();
      } catch (final Throwable ex) { // CHECKSTYLE:IGNORE .*
         throw new ReflectionException(ex);
      }
   }

   @SuppressWarnings("unchecked")
   public static <O, P> SetterAccessor<O, P> createPublicSetterAccessor(final Class<O> beanClass, final String propertyName, final Class<P> propertyType)
      throws ReflectionException {
      return createPublicMethodAccessor(SetterAccessor.class, beanClass, "set" + Strings.capitalize(propertyName), void.class, propertyType);
   }

   /**
    * Searches for public or non-public method with the exact signature
    */
   public static boolean existsAny(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      return findAny(clazz, methodName, parameterTypes) != null;
   }

   /**
    * Searches for public or non-public method with type compatible signature
    */
   public static boolean existsAnyCompatible(final Class<?> clazz, final String methodName, final Class<?>... argTypes) {
      return findAnyCompatible(clazz, methodName, argTypes) != null;
   }

   /**
    * Searches for public method with the exact signature
    */
   public static boolean existsPublic(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      return findPublic(clazz, methodName, parameterTypes) != null;
   }

   /**
    * Searches for public method with type compatible signature
    */
   public static boolean existsPublicCompatible(final Class<?> clazz, final String methodName, final Class<?>... argTypes) {
      return findPublicCompatible(clazz, methodName, argTypes) != null;
   }

   /**
    * Searches for public or non-public method with the exact signature
    */
   public static Method findAny(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);

      Class<?> currentClass = clazz;
      while (currentClass != null) {
         try {
            return currentClass.getDeclaredMethod(methodName, parameterTypes);
         } catch (final NoSuchMethodException e) {
            // ignore
         }
         currentClass = currentClass.getSuperclass();
      }
      return null;
   }

   /**
    * Searches for public or non-public method with type compatible signature
    */
   public static Method findAnyCompatible(final Class<?> clazz, final String methodName, final Class<?>... argTypes) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);

      // find exact match first
      final Method m = findAny(clazz, methodName, argTypes);
      if (m != null)
         return m;

      // if the exact match search found nothing for zero-argument method, then we can abort the search
      if (argTypes == null || argTypes.length == 0)
         return null;

      Class<?> currentClass = clazz;
      while (currentClass != null) {
         final Method[] declaredMethods = currentClass.getDeclaredMethods();
         for (final Method candidate : declaredMethods) {
            if (isStatic(candidate)) {
               continue;
            }
            if (!methodName.equals(candidate.getName())) {
               continue;
            }

            final Class<?>[] candidateParameterTypes = candidate.getParameterTypes();

            if (candidateParameterTypes.length != argTypes.length) {
               continue;
            }

            boolean isCompatible = true;
            for (int i = 0; i < argTypes.length; i++) {
               if (argTypes[i] == null) {
                  continue;
               }
               if (!Types.isAssignableTo(argTypes[i], candidateParameterTypes[i])) {
                  isCompatible = false;
                  break;
               }
            }
            if (isCompatible)
               return candidate;
         }
         currentClass = currentClass.getSuperclass();
      }
      return null;
   }

   /**
    * Searches for public or non-public method with type compatible signature
    */
   public static Method findAnyCompatible(final Class<?> clazz, final String methodName, final Object... args) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);

      if (args == null || args.length == 0)
         return findAny(clazz, methodName);

      final Class<?>[] argTypes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
         argTypes[i] = args[i] == null ? null : args[i].getClass();
      }

      return findAnyCompatible(clazz, methodName, argTypes);
   }

   /**
    * Searches for public or non-public getter
    */
   public static Method findAnyGetter(final Class<?> clazz, final String propertyName) {
      return findAnyGetter(clazz, propertyName, null);
   }

   /**
    * Searches for public or non-public getter with type compatible signature
    */
   public static Method findAnyGetter(final Class<?> clazz, final String propertyName, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", propertyName);

      final String appendix = propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

      Class<?> currentClass = clazz;

      while (currentClass != null) {

         if (compatibleTo == Boolean.class || compatibleTo == boolean.class) {
            try {
               final Method getter = currentClass.getDeclaredMethod("is" + appendix);
               if (Types.isAssignableTo(getter.getReturnType(), Boolean.class))
                  return getter;
            } catch (final NoSuchMethodException ex) {
               // ignore
            }
         }

         try {
            final Method getter = currentClass.getDeclaredMethod("get" + appendix);
            if (compatibleTo == null)
               return getter;
            if (Types.isAssignableTo(getter.getReturnType(), compatibleTo))
               return getter;
         } catch (final NoSuchMethodException ex) {
            // ignore
         }

         if (compatibleTo == null) {
            try {
               final Method getter = currentClass.getDeclaredMethod("is" + appendix);
               if (Types.isAssignableTo(getter.getReturnType(), Boolean.class))
                  return getter;
            } catch (final NoSuchMethodException ex) {
               // ignore
            }
         }

         currentClass = currentClass.getSuperclass();
      }

      return null;
   }

   /**
    * Searches for public or non-public setter
    */
   public static Method findAnySetter(final Class<?> clazz, final String propertyName) {
      return findAnySetter(clazz, propertyName, null);
   }

   /**
    * Searches for public or non-public setter with type compatible signature
    */
   public static Method findAnySetter(final Class<?> clazz, final String propertyName, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", propertyName);

      final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

      Class<?> currentClass = clazz;
      while (currentClass != null) {
         final Method[] declaredMethods = currentClass.getDeclaredMethods();
         for (final Method method : declaredMethods) {
            if (isStatic(method)) {
               continue;
            }

            if (!methodName.equals(method.getName())) {
               continue;
            }

            final Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) {
               continue;
            }

            if (compatibleTo == null)
               return method;

            if (Types.isAssignableTo(compatibleTo, paramTypes[0]))
               return method;
         }

         currentClass = currentClass.getSuperclass();
      }

      return null;
   }

   /**
    * Searches for public method with the exact signature
    */
   public static Method findPublic(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);
      try {
         return clazz.getMethod(methodName, parameterTypes);
      } catch (final NoSuchMethodException e) {
         return null;
      }
   }

   /**
    * Searches for public method with type compatible signature
    */
   public static Method findPublicCompatible(final Class<?> clazz, final String methodName, final Class<?>... argTypes) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);

      // find exact match first
      final Method m = findPublic(clazz, methodName, argTypes);
      if (m != null)
         return m;

      // if the exact match search found nothing for zero-argument method, then we can abort the search
      if (argTypes == null || argTypes.length == 0)
         return null;

      final Method[] publicMethods = clazz.getMethods();
      for (final Method candidate : publicMethods) {
         if (isStatic(candidate)) {
            continue;
         }
         if (!methodName.equals(candidate.getName())) {
            continue;
         }

         final Class<?>[] candidateParameterTypes = candidate.getParameterTypes();

         if (candidateParameterTypes.length != argTypes.length) {
            continue;
         }

         for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i] == null) {
               continue;
            }
            if (!Types.isAssignableTo(argTypes[i], candidateParameterTypes[i])) {
               break;
            }
         }
         return candidate;
      }
      return null;
   }

   /**
    * Searches for public method with type compatible signature
    */
   public static Method findPublicCompatible(final Class<?> clazz, final String methodName, final Object... args) {
      Args.notNull("clazz", clazz);
      Args.notNull("methodName", methodName);

      if (args == null || args.length == 0)
         return findPublic(clazz, methodName);

      final Class<?>[] argTypes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
         argTypes[i] = args[i] == null ? null : args[i].getClass();
      }

      return findPublicCompatible(clazz, methodName, argTypes);
   }

   /**
    * Searches for public getter
    */
   public static Method findPublicGetter(final Class<?> clazz, final String propertyName) {
      return findPublicGetter(clazz, propertyName, null);
   }

   /**
    * Searches for public getter with type compatible signature
    */
   public static Method findPublicGetter(final Class<?> clazz, final String propertyName, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);
      Args.notNull("propertyName", propertyName);

      final String appendix = propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

      if (compatibleTo == Boolean.class || compatibleTo == boolean.class) {
         try {
            final Method getter = clazz.getMethod("is" + appendix);
            if (Types.isAssignableTo(getter.getReturnType(), Boolean.class))
               return getter;
         } catch (final NoSuchMethodException ex) {
            // ignore
         }
      }

      try {
         final Method getter = clazz.getMethod("get" + appendix);
         if (compatibleTo == null)
            return getter;
         if (Types.isAssignableTo(getter.getReturnType(), compatibleTo))
            return getter;
      } catch (final NoSuchMethodException ex) {
         // ignore
      }

      if (compatibleTo == null) {
         try {
            final Method getter = clazz.getMethod("is" + appendix);
            if (Types.isAssignableTo(getter.getReturnType(), Boolean.class))
               return getter;
         } catch (final NoSuchMethodException ex) {
            // ignore
         }
      }

      LOG.trace("No public getter for [%s] found in class [%s] compatible to [%s].", propertyName, clazz, compatibleTo);
      return null;
   }

   /**
    * Searches for public setter
    */
   public static Method findPublicSetter(final Class<?> clazz, final String propertyName) {
      return findPublicSetter(clazz, propertyName, null);
   }

   /**
    * Searches for public setter with type compatible signature
    */
   public static Method findPublicSetter(final Class<?> clazz, final String propertyName, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);
      Args.notNull("propertyName", propertyName);

      final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

      final Method[] publicMethods = clazz.getMethods();
      for (final Method method : publicMethods) {
         if (isStatic(method)) {
            continue;
         }

         if (!methodName.equals(method.getName())) {
            continue;
         }

         final Class<?>[] paramTypes = method.getParameterTypes();
         if (paramTypes.length != 1) {
            continue;
         }

         if (compatibleTo == null)
            return method;

         if (Types.isAssignableTo(compatibleTo, paramTypes[0]))
            return method;
      }
      LOG.trace("No public setter for [%s] found in class [%s] comaptible to [%s].", propertyName, clazz, compatibleTo);
      return null;
   }

   public static Method findSuper(final Method method) {
      Args.notNull("method", method);

      // static methods cannot be overridden
      if (isStatic(method))
         return null;

      final String methodName = method.getName();
      final Class<?>[] parameterTypes = method.getParameterTypes();

      Class<?> currentClass = method.getDeclaringClass().getSuperclass();
      while (currentClass != null) {
         final Method m = findAny(currentClass, methodName, parameterTypes);
         if (m != null && !isPrivate(m))
            return m;
         currentClass = currentClass.getSuperclass();
      }
      return null;
   }

   /**
    * Recursively collects all getters (public and non-public), excluding references to overridden methods.
    */
   public static List<Method> getAllGetters(final Class<?> clazz) {
      final Set<String> overridableGetterNames = CollectionUtils.newHashSet();
      final List<Method> result = CollectionUtils.newArrayList();

      Class<?> currentClass = clazz;
      while (currentClass != null) {
         for (final Method m : currentClass.getDeclaredMethods()) {
            if (!isGetter(m)) {
               continue;
            }

            if (isPrivate(m)) {
               result.add(m);
               continue;
            }

            final String name = m.getName();
            if (!overridableGetterNames.contains(name)) {
               overridableGetterNames.add(name);
               result.add(m);
            }
         }
         currentClass = currentClass.getSuperclass();
      }
      return result;
   }

   /**
    * Recursively collects all setters (public and non-public), excluding references to overridden methods.
    */
   public static List<Method> getAllSetters(final Class<?> clazz) {
      final Set<String> overridableSetterNames = CollectionUtils.newHashSet();
      final List<Method> result = CollectionUtils.newArrayList();

      Class<?> currentClass = clazz;
      while (currentClass != null) {
         for (final Method m : currentClass.getDeclaredMethods()) {
            if (!isSetter(m)) {
               continue;
            }

            if (isPrivate(m)) {
               result.add(m);
               continue;
            }

            final String name = m.getName();
            if (!overridableSetterNames.contains(name)) {
               overridableSetterNames.add(name);
               result.add(m);
            }
         }
         currentClass = currentClass.getSuperclass();
      }
      return result;
   }

   /**
    * @return a list of all methods declared on super type interfaces this method implements/overrides
    */
   public static List<Method> getInterfaceMethods(final Method method) {
      Args.notNull("method", method);

      // static methods cannot be overridden
      if (isStatic(method))
         return Collections.emptyList();

      final Set<Class<?>> interfaces = Types.getInterfacesRecursive(method.getDeclaringClass());
      if (interfaces.isEmpty())
         return Collections.emptyList();

      final String methodName = method.getName();
      final Class<?>[] parameterTypes = method.getParameterTypes();

      final List<Method> methods = newArrayList();
      for (final Class<?> iface : interfaces) {
         final Method m = findPublic(iface, methodName, parameterTypes);
         if (m != null) {
            methods.add(m);
         }
      }
      return methods;
   }

   public static List<Method> getPublic(final Class<?> clazz) {
      return Arrays.asList(clazz.getMethods());
   }

   public static List<Method> getPublicGetters(final Class<?> clazz) {
      final List<Method> result = CollectionUtils.newArrayList();
      for (final Method m : clazz.getMethods())
         if (isGetter(m)) {
            result.add(m);
         }
      return result;
   }

   /**
    * Searches for public getters with type compatible signature
    */
   public static List<Method> getPublicGetters(final Class<?> clazz, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);

      final List<Method> result = CollectionUtils.newArrayList();

      final Method[] publicMethods = clazz.getMethods();
      for (final Method method : publicMethods) {
         if (isStatic(method)) {
            continue;
         }

         if (!isGetter(method)) {
            continue;
         }

         if (compatibleTo == null || Types.isAssignableTo(compatibleTo, method.getReturnType())) {
            result.add(method);
         }
      }

      return result;
   }

   public static List<Method> getPublicSetters(final Class<?> clazz) {
      final List<Method> result = CollectionUtils.newArrayList();
      for (final Method m : clazz.getMethods())
         if (isSetter(m)) {
            result.add(m);
         }
      return result;
   }

   /**
    * Searches for public setters with type compatible signature
    */
   public static List<Method> getPublicSetters(final Class<?> clazz, final Class<?> compatibleTo) {
      Args.notNull("clazz", clazz);

      final List<Method> result = CollectionUtils.newArrayList();

      final Method[] publicMethods = clazz.getMethods();
      for (final Method method : publicMethods) {
         if (isStatic(method)) {
            continue;
         }

         if (!isSetter(method)) {
            continue;
         }

         final Class<?>[] paramTypes = method.getParameterTypes();

         if (compatibleTo == null || Types.isAssignableTo(compatibleTo, paramTypes[0])) {
            result.add(method);
         }
      }

      return result;
   }

   /**
    * @param obj may be null for invoking static methods
    */
   @SuppressWarnings("unchecked")
   public static <T> T invoke(final Object obj, final Method method, final Object... args) throws InvokingMethodFailedException {
      Args.notNull("method", method);

      try {
         ensureAccessible(method);
         return (T) method.invoke(obj, args);
      } catch (final Exception ex) {
         throw new InvokingMethodFailedException(method, obj, ex);
      }
   }

   @SuppressWarnings("unchecked")
   public static <T> T invoke(final Object obj, final String methodName, final Object... args) throws InvokingMethodFailedException {
      Args.notNull("obj", obj);
      Args.notNull("methodName", methodName);

      final Method method = Methods.findAnyCompatible(obj.getClass(), methodName, args);
      if (method == null)
         throw new IllegalArgumentException("No method [" + methodName + "] with compatible signature found.");

      try {
         ensureAccessible(method);
         return (T) method.invoke(obj, args);
      } catch (final Exception ex) {
         throw new InvokingMethodFailedException(method, obj, ex);
      }
   }

   /**
    * determines if a method is a JavaBean style getter method
    */
   public static boolean isGetter(final Method method) {
      Args.notNull("method", method);

      if (method.getParameterTypes().length > 0)
         return false;

      if (isReturningVoid(method))
         return false;

      final String methodName = method.getName();

      if (methodName.startsWith("get")) {
         if (methodName.length() == 3)
            return false;
         if (!Character.isUpperCase(methodName.charAt(3)))
            return false;
         return true;
      }
      if (methodName.startsWith("is")) {
         if (methodName.length() == 2)
            return false;
         if (!Character.isUpperCase(methodName.charAt(2)))
            return false;
         return method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class;
      }
      return false;
   }

   /**
    * determines if a method is a void method
    */
   public static boolean isReturningVoid(final Method method) {
      Args.notNull("method", method);

      return method.getReturnType() == void.class;
   }

   /**
    * determines if a method is a JavaBean style setter method
    */
   public static boolean isSetter(final Method method) {
      Args.notNull("method", method);

      final Class<?>[] methodParameterTypes = method.getParameterTypes();

      if (methodParameterTypes.length != 1)
         return false;

      if (!isReturningVoid(method) && //
         !Types.isAssignableTo(method.getReturnType(), method.getDeclaringClass()) // in case of fluent-api
      )
         return false;

      final String methodName = method.getName();

      if (methodName.length() < 4)
         return false;

      if (!methodName.startsWith("set"))
         return false;

      if (!Character.isUpperCase(methodName.charAt(3)))
         return false;

      return true;
   }
}
