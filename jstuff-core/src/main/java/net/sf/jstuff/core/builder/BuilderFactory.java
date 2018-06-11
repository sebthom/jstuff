/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.builder;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.reflection.Annotations;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.reflection.visitor.DefaultClassVisitor;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE extends Builder<? extends TARGET_CLASS>> {

    public static //
    <TARGET_CLASS, BUILDER_INTERFACE extends Builder<? extends TARGET_CLASS>> //
    BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE> of(final Class<BUILDER_INTERFACE> builderInterface, final Object... constructorArgs) {
        return new BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE>(builderInterface, null, constructorArgs);
    }

    /**
     * @param targetClass if <code>null</code> the builder factory tries to extract the generic argument type information from the builderInterface class
     */
    public static //
    <TARGET_CLASS, BUILDER_INTERFACE extends Builder<? extends TARGET_CLASS>> //
    BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE> of(final Class<BUILDER_INTERFACE> builderInterface, final Class<TARGET_CLASS> targetClass,
            final Object... constructorArgs) {
        return new BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE>(builderInterface, targetClass, constructorArgs);
    }

    private final Class<BUILDER_INTERFACE> builderInterface;
    private final Class<TARGET_CLASS> targetClass;
    private final Object[] constructorArgs;

    @SuppressWarnings("unchecked")
    protected BuilderFactory(final Class<BUILDER_INTERFACE> builderInterface, final Class<TARGET_CLASS> targetClass, final Object... constructorArgs) {
        Args.notNull("builderInterface", builderInterface);
        if (!builderInterface.isInterface())
            throw new IllegalArgumentException("[builderInterface] must be an interface!");

        this.builderInterface = builderInterface;

        this.targetClass = targetClass == null ? (Class<TARGET_CLASS>) Types.findGenericTypeArguments(builderInterface, Builder.class)[0] : targetClass;

        Args.notNull("targetClass", this.targetClass);

        if (this.targetClass.isInterface())
            throw new IllegalArgumentException("Target class [" + this.targetClass.getName() + "] is an interface.");

        if (Types.isAbstract(this.targetClass))
            throw new IllegalArgumentException("Target class [" + this.targetClass.getName() + "] is abstract.");

        this.constructorArgs = constructorArgs;
    }

    private static final class BuilderImpl implements InvocationHandler {
        final Map<String, Object[]> properties = Maps.newHashMap();

        private final Class<?> builderInterface;
        private final Class<?> targetClass;
        private final Object[] constructorArgs;

        private Builder.Property propertyDefaults;
        private final Map<String, Builder.Property> propertyConfig = Maps.newHashMap(2);
        private final List<Method> onPostBuilds = newArrayList(2);

        public BuilderImpl(final Class<?> builderInterface, final Class<?> targetClass, final Object[] constructorArgs) {
            this.builderInterface = builderInterface;
            this.targetClass = targetClass;
            this.constructorArgs = constructorArgs;

            // collecting annotation information
            Types.visit(builderInterface, new DefaultClassVisitor() {

                @Override
                public boolean visit(final Class<?> clazz) {
                    if (propertyDefaults == null) {
                        propertyDefaults = clazz.getAnnotation(Builder.Property.class);
                    }
                    return true;
                }

                @Override
                public boolean isVisitingFields(final Class<?> clazz) {
                    return false;
                }

                @Override
                public boolean isVisitingMethod(final Method method) {
                    if (Methods.isStatic(method))
                        return false;

                    if (method.getParameterTypes().length != 1)
                        return false;

                    final Builder.Property annoOverride = propertyConfig.get(method.getName());
                    if (annoOverride == null) {
                        propertyConfig.put(method.getName(), method.getAnnotation(Builder.Property.class));
                    }
                    return false;
                }
            });
            if (propertyDefaults == null) {
                propertyDefaults = Annotations.getDefaults(Builder.Property.class);
            }

            // collecting @OnPostBuild methods
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
                        onPostBuilds.add(method);
                    }
                    return false;
                }
            });
        };

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final boolean isBuildMethod = "build".equals(method.getName()) //
                    && method.getParameterTypes().length == 0 //
                    && method.getReturnType().isAssignableFrom(targetClass);

            if (isBuildMethod) {

                // creating target instance
                final Object target = Types.newInstance(targetClass, constructorArgs);

                // writing properties
                for (final Entry<String, Object[]> property : properties.entrySet()) {
                    String propName = property.getKey();
                    // remove "with" prefix from withSomeProperty(...) named properties
                    if (propName.length() > 4 && propName.startsWith("with")) {
                        propName = Strings.lowerCaseFirstChar(propName.substring(4));
                    }
                    final Object[] propArgs = property.getValue();
                    final String setterName = "set" + Strings.upperCaseFirstChar(propName);
                    final Method setterMethod = Methods.findAnyCompatible(targetClass, setterName, propArgs);
                    // if no setter found then directly try to set the field
                    if (setterMethod == null && propArgs.length == 1) {
                        Types.writePropertyIgnoringFinal(target, propName, propArgs[0]);
                    } else {
                        Assert.notNull(setterMethod, "Method [%s#%s()] not found.", targetClass.getName(), setterName);
                        Methods.invoke(target, setterMethod, propArgs);
                    }
                }

                // validate @Property constraints
                if (propertyConfig.size() > 0) {
                    for (final Entry<String, Builder.Property> prop : propertyConfig.entrySet()) {
                        final String propName = prop.getKey();
                        final Builder.Property propConfig = prop.getValue();

                        if (properties.containsKey(propName)) {
                            final boolean isNullable = propConfig == null ? propertyDefaults.nullable() : propConfig.nullable();
                            if (!isNullable && (properties.get(propName) == null || properties.get(propName)[0] == null))
                                throw new IllegalArgumentException(builderInterface.getSimpleName() + "." + propName + "(...) must not be set to null.");
                        } else {
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
                        if (ex.getCause() instanceof InvocationTargetException)
                            throw ex.getCause().getCause();
                        throw ex;
                    }
                }
                return target;
            }

            if ("toString".equals(method.getName()) && method.getParameterTypes().length == 0)
                return builderInterface.getName() + "@" + hashCode();

            if (method.getReturnType().isAssignableFrom(builderInterface)) {
                properties.put(method.getName(), args);
                return proxy;
            }
            throw new UnsupportedOperationException(method.toString());
        }
    }

    public BUILDER_INTERFACE create() {
        return Proxies.create(new BuilderImpl(builderInterface, targetClass, constructorArgs), builderInterface);
    }
}
