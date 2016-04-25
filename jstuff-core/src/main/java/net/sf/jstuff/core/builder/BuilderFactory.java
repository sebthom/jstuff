/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.core.builder;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import net.sf.jstuff.core.reflection.Annotations;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.reflection.visitor.DefaultClassVisitor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderFactory<TARGET_CLASS, BUILDER_INTERFACE extends Builder<? extends TARGET_CLASS>> {

    /**
     * @param builderInterface
     * @param targetClass if null builder factory tries to extract the generic argument type information from the builderInterface class
     * @param constructorArgs
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

        if (targetClass == null)
            throw new IllegalArgumentException("Target class is not specified.");
        if (targetClass.isInterface())
            throw new IllegalArgumentException("Target class [" + targetClass.getName() + "] is an interface.");
        if (Types.isAbstract(targetClass))
            throw new IllegalArgumentException("Target class [" + targetClass.getName() + "] is abstract.");

        this.constructorArgs = constructorArgs;
    }

    public BUILDER_INTERFACE create() {
        return Proxies.create(new InvocationHandler() {
            final Map<String, Object> properties = newHashMap();

            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if ("build".equals(method.getName()) && method.getParameterTypes().length == 0 && method.getReturnType().isAssignableFrom(targetClass)) {
                    final TARGET_CLASS target = Types.newInstance(targetClass, constructorArgs);
                    for (final Entry<String, Object> property : properties.entrySet()) {
                        Types.writeProperty(target, property.getKey(), property.getValue());
                    }

                    // collecting @OnPostBuild methods
                    final List<Method> onPostBuilds = newArrayList(2);
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
                            return !Methods.isAbstract(method) && !Methods.isStatic(method) && Annotations.exists(method, OnPostBuild.class, false);
                        }

                        @Override
                        public boolean visit(final Method method) {
                            onPostBuilds.add(method);
                            return true;
                        }
                    });

                    // invoking @OnPostBuild methods
                    for (int i = onPostBuilds.size() - 1; i >= 0; i--) {
                        try {
                            Methods.invoke(target, onPostBuilds.get(i), ArrayUtils.EMPTY_OBJECT_ARRAY);
                        } catch (final InvokingMethodFailedException ex) {
                            if (ex.getCause() instanceof InvocationTargetException)
                                throw (RuntimeException) ex.getCause().getCause();
                            throw ex;
                        }
                    }
                    return target;
                }

                if ("toString".equals(method.getName()) && method.getParameterTypes().length == 0)
                    return builderInterface.getName() + "@" + hashCode();

                if (method.getParameterTypes().length == 1 && method.getReturnType().isAssignableFrom(builderInterface)) {
                    properties.put(method.getName(), args[0]);
                    return proxy;
                }
                throw new UnsupportedOperationException(method.toString());
            }
        }, builderInterface);
    }
}
