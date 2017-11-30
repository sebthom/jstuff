/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.ArrayUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.reflection.visitor.ClassVisitor;
import net.sf.jstuff.core.reflection.visitor.ClassVisitorWithTypeArguments;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Types {
    private static Logger LOG = Logger.create();

    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object obj) {
        return (T) obj;
    }

    public static <T> T createMixin(final Class<T> objectInterface, final Object... mixins) {
        Args.notNull("objectInterface", objectInterface);
        Args.notEmpty("mixins", mixins);

        return Proxies.create(new InvocationHandler() {
            final Map<Method, Tuple2<Object, Method>> mappedMethodsCache = new ConcurrentHashMap<Method, Tuple2<Object, Method>>();

            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                Tuple2<Object, Method> mixedInMethod = mappedMethodsCache.get(method);
                if (mixedInMethod == null) {
                    for (final Object mixin : mixins) {
                        final Method methodImpl = Methods.findAny(mixin.getClass(), method.getName(), method.getParameterTypes());
                        if (methodImpl != null) {
                            mixedInMethod = Tuple2.create(mixin, methodImpl);
                            mappedMethodsCache.put(method, mixedInMethod);
                            break;
                        }
                    }
                }
                if (mixedInMethod == null)
                    throw new UnsupportedOperationException("Method is not implemented.");
                return Methods.invoke(mixedInMethod.get1(), mixedInMethod.get2(), args);
            }
        }, objectInterface);
    }

    public static <T> T createSynchronized(final Class<T> objectInterface, final T object) {
        Args.notNull("objectInterface", objectInterface);
        Args.notNull("object", object);

        return createSynchronized(objectInterface, object, object);
    }

    public static <T> T createSynchronized(final Class<T> objectInterface, final T object, final Object lock) {
        Args.notNull("objectInterface", objectInterface);
        Args.notNull("object", object);

        return Proxies.create(new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                synchronized (lock) {
                    return method.invoke(object, args);
                }
            }
        }, objectInterface);
    }

    public static <T> T createThreadLocalized(final Class<T> objectInterface, final ThreadLocal<T> threadLocal) {
        Args.notNull("objectInterface", objectInterface);
        Args.notNull("threadLocal", threadLocal);

        return Proxies.create(new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return method.invoke(threadLocal.get(), args);
            }
        }, objectInterface);
    }

    /**
     * @return null if class not found
     */
    public static <T> Class<T> find(final String className) {
        return find(className, true);
    }

    /**
     * @return null if class not found
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> find(final String className, final boolean initialize) {
        Args.notNull("className", className);

        LOG.trace("Trying to load class [%s]...", className);
        try {
            return (Class<T>) Class.forName(className, initialize, null);
        } catch (final ClassNotFoundException ex) {
            // ignore
        } catch (final NoClassDefFoundError ex) {
            LOG.debug(ex);
        }

        final ClassLoader cl1 = Types.class.getClassLoader();
        if (cl1 != null) {
            try {
                return (Class<T>) Class.forName(className, initialize, cl1);
            } catch (final ClassNotFoundException ex) {
                // ignore
            } catch (final NoClassDefFoundError ex) {
                LOG.debug(ex);
            }
        }

        final ClassLoader cl2 = Thread.currentThread().getContextClassLoader();
        if (cl1 != cl2 && cl2 != null) {
            try {
                return (Class<T>) Class.forName(className, initialize, cl2);
            } catch (final ClassNotFoundException ex) {
                // ignore
            } catch (final NoClassDefFoundError ex) {
                LOG.debug(ex);
            }
        }
        return null;
    }

    public static <T> Type[] findGenericTypeArguments(final Class<? extends T> searchIn, final Class<T> searchFor) {
        Args.notNull("searchIn", searchIn);
        Args.notNull("searchFor", searchFor);

        // if the searchFor type is not a generic type there is nothing to find
        if (searchFor.getTypeParameters().length == 0)
            return ArrayUtils.EMPTY_CLASS_ARRAY;

        if (!searchFor.isAssignableFrom(searchIn))
            throw new IllegalArgumentException("Class [searchIn=" + searchIn.getName() + "] is not assignable to [searchFor=" + searchFor.getName() + "]");

        final boolean isSearchForInterface = searchFor.isInterface();

        /*
         * traverse the class hierarchy and collect generic variable => concrete variable argument (type) mappings
         */
        final Map<TypeVariable<?>, Type> genericVariableToArgumentMappings = newHashMap();
        final ParameterizedType[] searchForType = { null };

        visit(searchIn, new ClassVisitorWithTypeArguments() {
            public boolean isVisiting(final Class<?> clazz, final ParameterizedType type) {
                return searchFor.isAssignableFrom(clazz);
            }

            public boolean isVisitingInterfaces(final Class<?> clazz, final ParameterizedType type) {
                return isSearchForInterface && searchFor.isAssignableFrom(clazz);
            }

            public boolean isVisitingSuperclass(final Class<?> clazz, final ParameterizedType type) {
                return searchFor.isAssignableFrom(clazz);
            }

            public boolean visit(final Class<?> clazz, final ParameterizedType type) {
                if (type != null) {
                    CollectionUtils.putAll(genericVariableToArgumentMappings, //
                        /*generic variable*/(TypeVariable<?>[]) clazz.getTypeParameters(), //
                        /*arguments (concrete types) for generic variables*/type.getActualTypeArguments() //
                    );
                }

                if (clazz == searchFor) {
                    searchForType[0] = type;
                    return false;
                }
                return true;
            }
        });

        /*
         * build the result list based on the information collected in genericVariableToTypeMappings
         */
        final Type[] genericVariables;
        if (searchForType[0] == null) {
            genericVariables = searchFor.getTypeParameters();
        } else {
            genericVariables = searchForType[0].getActualTypeArguments();
        }
        final Class<?>[] res = new Class<?>[genericVariables.length];
        for (int i = 0, l = genericVariables.length; i < l; i++) {
            Type genericVariable = genericVariables[i];
            while (genericVariableToArgumentMappings.containsKey(genericVariable)) {
                genericVariable = genericVariableToArgumentMappings.get(genericVariable);
            }
            res[i] = resolveUnderlyingClass(genericVariable);
        }
        return res;
    }

    /**
     * @return the local JAR or root directory containing the given class
     */
    public static File findLibrary(final Class<?> clazz) {
        try {
            final CodeSource cs = clazz.getProtectionDomain().getCodeSource();
            if (cs != null && cs.getLocation() != null)
                return new File(cs.getLocation().toURI());
        } catch (final SecurityException ex) {
            // ignore
        } catch (final URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        /*
         * fallback mechanism in case CodeSource.getLocation() is null (should only be the case for JDK classes) or a SecurityManager is active
         */
        final URL location = clazz.getResource(clazz.getSimpleName() + ".class");
        if (location == null)
            return null;

        try {
            // extract the jar path from: jar:file:/F:/allianz/apps/dev/java/sun_jdk1.5.0_22/jre/lib/rt.jar!/java/lang/String.class
            return new File(URLDecoder.decode(Strings.substringBetween(location.getPath(), "file:", "!"), Charset.defaultCharset().name()));
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param clazz the class to inspect
     * @return a set with all implemented interfaces
     */
    public static Set<Class<?>> getInterfacesRecursive(final Class<?> clazz) {
        Args.notNull("clazz", clazz);

        return getInterfacesRecursive(clazz, new HashSet<Class<?>>(2));
    }

    private static Set<Class<?>> getInterfacesRecursive(Class<?> clazz, final Set<Class<?>> result) {
        while (clazz != null) {
            for (final Class<?> next : clazz.getInterfaces()) {
                result.add(next);
                getInterfacesRecursive(next, result);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    public static Class<?> getPrimitiveWrapper(final Class<?> primitive) {
        if (!primitive.isPrimitive())
            return primitive;

        if (boolean.class == primitive)
            return Boolean.class;

        if (byte.class == primitive)
            return Byte.class;
        if (short.class == primitive)
            return Short.class;
        if (int.class == primitive)
            return Integer.class;
        if (long.class == primitive)
            return Long.class;

        if (float.class == primitive)
            return Float.class;
        if (double.class == primitive)
            return Double.class;

        if (char.class == primitive)
            return Character.class;

        if (void.class == primitive)
            return Void.class;

        throw new RuntimeException("Unknown primitive type [" + primitive + "]");
    }

    /**
     * @return the implementation version of the archive containing the class
     */
    public static String getVersion(final Class<?> clazz) {
        /*
         * get version from META-INF/MANIFEST.MF
         */
        {
            final String version = Strings.trim(clazz.getPackage().getImplementationVersion());
            if (!Strings.isEmpty(version))
                return version;
        }

        final File location = findLibrary(clazz);
        if (location == null)
            return null;

        /*
         * get version from META-INF/maven/<groupId>/<artifactId>/pom.properties
         */
        if (location.isFile()) {
            ZipFile jar = null;
            try {
                jar = new ZipFile(location);
                for (final Enumeration<? extends ZipEntry> jarEntries = jar.entries(); jarEntries.hasMoreElements();) {
                    final ZipEntry jarEntry = jarEntries.nextElement();
                    if (jarEntry.isDirectory()) {
                        continue;
                    }

                    final String jarEntryName = jarEntry.getName();

                    if (jarEntryName.length() < 25 || !jarEntryName.startsWith("META-INF/maven")) {
                        continue;
                    }

                    if (jarEntryName.endsWith("/pom.properties")) {
                        final InputStream is = jar.getInputStream(jarEntry);
                        try {
                            final Properties p = new Properties();
                            p.load(jar.getInputStream(jarEntry));
                            final String version = Strings.trim(p.getProperty("version"));
                            if (!Strings.isEmpty(version))
                                return version;
                            break;
                        } finally {
                            IOUtils.closeQuietly(is);
                        }
                    }
                }
            } catch (final IOException ex) {
                LOG.debug(ex, "Unexpected exception while accessing JAR");
            } finally {
                IOUtils.closeQuietly(jar);
            }

            /*
             * get version from jar file name
             */
            {
                final String version = Strings.trim(Strings.substringBeforeLast(Strings.substringAfterLast(location.getName(), "-"), "."));
                if (!Strings.isEmpty(version))
                    return version;
            }
        }

        return null;
    }

    /**
     * initializes the given class
     */
    public static <T> Class<T> initialize(final Class<T> type) {
        Args.notNull("type", type);
        try {
            Class.forName(type.getName(), true, type.getClassLoader());
        } catch (final ClassNotFoundException e) {
            // should never happen
        }
        return type;
    }

    public static boolean isAbstract(final Class<?> type) {
        Args.notNull("type", type);

        return (type.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    /**
     * @return true if an object of type <code>fromType</code> can be used as value for a field or parameter of type <code>toType</code>
     */
    public static boolean isAssignableTo(final Class<?> fromType, final Class<?> toType) {
        Args.notNull("fromType", fromType);
        Args.notNull("toType", toType);

        return getPrimitiveWrapper(toType).isAssignableFrom(getPrimitiveWrapper(fromType));
    }

    public static boolean isInstanceOf(final Object obj, final Class<?> type) {
        Args.notNull("type", type);
        if (obj == null)
            return false;

        return isAssignableTo(obj.getClass(), type);
    }

    public static boolean isAvailable(final String className) {
        return find(className, false) != null;
    }

    public static boolean isInnerClass(final Class<?> type) {
        Args.notNull("type", type);

        return type.getName().indexOf('$') > -1;
    }

    public static boolean isNonStaticInnerClass(final Class<?> type) {
        return isInnerClass(type) && (type.getModifiers() & Modifier.STATIC) == 0;
    }

    public static boolean isScalar(final Class<?> type) {
        Args.notNull("type", type);

        return type.isPrimitive() || type == Boolean.class || //
                type == Character.class || //
                Enum.class.isAssignableFrom(type) || //
                Number.class.isAssignableFrom(type) || //
                CharSequence.class.isAssignableFrom(type) || //
                Date.class.isAssignableFrom(type);
    }

    /**
     * Determines if all given classes are visible to the given classloader
     */
    public static boolean isVisible(final ClassLoader cl, final Class<?>... classes) {
        try {
            for (final Class<?> clazz : classes)
                if (clazz != cl.loadClass(clazz.getName())) //
                    return false;
            return true;
        } catch (final ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * Determines if the given class is visible to the given classloader
     */
    public static boolean isVisible(final ClassLoader cl, final Class<?> clazz) {
        try {
            return clazz == cl.loadClass(clazz.getName());
        } catch (final ClassNotFoundException ex) {
            return false;
        }
    }

    public static <T> T newInstance(final Class<T> type, final Object... constructorArgs) {
        final Constructor<T> ctor = Constructors.findCompatible(type, constructorArgs);
        if (ctor == null)
            throw new IllegalArgumentException("No constructor found in class [" + type.getName() + "] compatible with give arguments!");
        return Constructors.invoke(ctor, constructorArgs);
    }

    /**
     * Tries to read the given value using a getter method or direct field access
     */
    public static <T> T readProperty(final Object obj, final String propertyName, final Class<? extends T> compatibleTo) throws ReflectionException {
        Args.notNull("obj", obj);
        Args.notNull("propertyName", propertyName);

        final Class<?> clazz = obj.getClass();

        final Method getter = Methods.findAnyGetter(clazz, propertyName, compatibleTo);
        if (getter != null)
            return Methods.invoke(obj, getter);

        final Field field = Fields.findRecursive(clazz, propertyName, compatibleTo);
        if (field != null)
            return Fields.read(obj, field);

        throw new ReflectionException("No corresponding getter method or field found for property [" + propertyName + "] in class [" + clazz + "]");
    }

    public static Type resolveBound(final TypeVariable<?> typeVariable) {
        final Type[] bounds = typeVariable.getBounds();
        if (bounds.length == 0)
            return null;

        Type bound = bounds[0];
        if (bound instanceof TypeVariable) {
            bound = resolveBound((TypeVariable<?>) bound);
        }

        return bound == Object.class ? null : bound;
    }

    public static Class<?> resolveUnderlyingClass(final Type type) {
        if (type instanceof Class)
            return (Class<?>) type;
        if (type instanceof ParameterizedType)
            return resolveUnderlyingClass(((ParameterizedType) type).getRawType());
        if (type instanceof GenericArrayType) {
            final Type ctype = ((GenericArrayType) type).getGenericComponentType();
            final Class<?> cclass = resolveUnderlyingClass(ctype);
            if (cclass != null)
                return Array.newInstance(cclass, 0).getClass();
        }
        if (type instanceof TypeVariable)
            return (Class<?>) resolveBound((TypeVariable<?>) type);

        return null;
    }

    public static void visit(final Class<?> clazz, final ClassVisitor visitor) {
        Args.notNull("clazz", clazz);
        Args.notNull("visitor", visitor);

        final Queue<Class<?>> toVisit = new LinkedList<Class<?>>();
        toVisit.add(clazz);
        while (!toVisit.isEmpty()) {
            final Class<?> current = toVisit.poll();

            if (!visitor.visit(current))
                return;

            if (visitor.isVisitingFields(current)) {
                for (final Field f : current.getDeclaredFields())
                    if (visitor.isVisitingField(f) && !visitor.visit(f))
                        return;
            }

            if (visitor.isVisitingMethods(current)) {
                for (final Method m : current.getDeclaredMethods())
                    if (visitor.isVisitingMethod(m) && !visitor.visit(m))
                        return;
            }

            if (visitor.isVisitingSuperclass(current)) {
                final Class<?> sclass = current.getSuperclass();
                if (sclass != null && visitor.isVisiting(sclass)) {
                    toVisit.add(sclass);
                }
            }
            if (visitor.isVisitingInterfaces(current)) {
                for (final Class<?> iface : current.getInterfaces())
                    if (visitor.isVisiting(iface)) {
                        toVisit.add(iface);
                    }
            }
        }
    }

    public static void visit(final Class<?> clazz, final ClassVisitorWithTypeArguments visitor) {
        Args.notNull("clazz", clazz);
        Args.notNull("visitor", visitor);

        final Queue<Type> toVisit = new LinkedList<Type>();
        toVisit.add(clazz);
        while (!toVisit.isEmpty()) {
            final Type current = toVisit.poll();

            final Class<?> currentClass;
            final ParameterizedType currentType;
            if (current instanceof ParameterizedType) {
                currentType = (ParameterizedType) current;
                currentClass = (Class<?>) currentType.getRawType();
            } else {
                currentType = null;
                currentClass = (Class<?>) current;
            }

            if (!visitor.visit(currentClass, currentType))
                return;

            if (visitor.isVisitingSuperclass(currentClass, currentType)) {
                final Type sclass = currentClass.getGenericSuperclass();
                if (sclass != null)
                    if (sclass instanceof ParameterizedType) {
                    final ParameterizedType sptype = (ParameterizedType) sclass;
                    if (visitor.isVisiting((Class<?>) sptype.getRawType(), sptype)) {
                    toVisit.add(sclass);
                    }
                    } else if (visitor.isVisiting((Class<?>) sclass, null)) {
                    toVisit.add(sclass);
                    }
            }

            if (visitor.isVisitingInterfaces(currentClass, currentType)) {
                for (final Type itype : currentClass.getGenericInterfaces())
                    if (itype instanceof ParameterizedType) {
                        final ParameterizedType iptype = (ParameterizedType) itype;
                        if (visitor.isVisiting((Class<?>) iptype.getRawType(), iptype)) {
                            toVisit.add(itype);
                        }
                    } else if (visitor.isVisiting((Class<?>) itype, null)) {
                        toVisit.add(itype);
                    }
            }
        }
    }

    /**
     * Tries to write the given value using a setter method or direct field access
     */
    public static void writeProperty(final Object obj, final String propertyName, final Object value) throws ReflectionException {
        Args.notNull("obj", obj);
        Args.notNull("propertyName", propertyName);

        final Class<?> clazz = obj.getClass();
        final Class<?> valueClazz = value == null ? null : value.getClass();

        final Method setter = Methods.findAnySetter(clazz, propertyName, valueClazz);
        if (setter != null) {
            Methods.invoke(obj, setter, value);
            return;
        }

        final Field field = Fields.findRecursive(clazz, propertyName, valueClazz);
        if (field != null) {
            Fields.write(obj, field, value);
            return;
        }
        throw new ReflectionException("No corresponding getter method or field found for property [" + propertyName + "] in class [" + clazz + "]");
    }

    /**
     * Tries to write the given value using a setter method or direct field access
     */
    public static void writePropertyIgnoringFinal(final Object obj, final String propertyName, final Object value) throws ReflectionException {
        Args.notNull("obj", obj);
        Args.notNull("propertyName", propertyName);

        final Class<?> clazz = obj.getClass();
        final Class<?> valueClazz = value == null ? null : value.getClass();

        final Method setter = Methods.findAnySetter(clazz, propertyName, valueClazz);
        if (setter != null) {
            Methods.invoke(obj, setter, value);
            return;
        }

        final Field field = Fields.findRecursive(clazz, propertyName, valueClazz);
        if (field != null) {
            Fields.writeIgnoringFinal(obj, field, value);
            return;
        }
        throw new ReflectionException("No corresponding setter method or field found for property [" + propertyName + "] in class [" + clazz + "]");
    }
}
