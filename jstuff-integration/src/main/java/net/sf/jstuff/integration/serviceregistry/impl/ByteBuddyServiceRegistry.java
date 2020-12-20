/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import java.lang.reflect.Method;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.integration.serviceregistry.ServiceUnavailableException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteBuddyServiceRegistry extends DefaultServiceRegistry {
   protected static final class ByteBuddyServiceInterceptor<SERVICE_INTERFACE> {
      private final ServiceEndpointState serviceEndpointState;
      private final Class<SERVICE_INTERFACE> serviceInterface;

      protected ByteBuddyServiceInterceptor(final ServiceEndpointState serviceEndpointState, final Class<SERVICE_INTERFACE> serviceInterface) {
         this.serviceEndpointState = serviceEndpointState;
         this.serviceInterface = serviceInterface;
      }

      @RuntimeType
      public Object intercept(@Origin final Method method, @AllArguments final Object... args) throws Exception {
         final Object service = serviceEndpointState.getActiveServiceIfCompatible(serviceInterface);
         if (service == null)
            throw new ServiceUnavailableException(serviceEndpointState.getServiceEndpointId(), serviceInterface);
         return method.invoke(service, args);
      }
   }

   @Override
   @SuppressWarnings("rawtypes")
   protected <SERVICE_INTERFACE> ServiceProxyInternal<SERVICE_INTERFACE> createServiceProxy(final ServiceEndpointState serviceEndpointState,
      final Class<SERVICE_INTERFACE> serviceInterface) {

      final MethodDelegation interceptor = MethodDelegation.to(new ByteBuddyServiceInterceptor<>(serviceEndpointState, serviceInterface));

      DynamicType.Builder builder = new ByteBuddy() //
         .subclass(DefaultServiceProxyAdvice.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_PUBLIC) //
         .implement(serviceInterface) //
         .method(ElementMatchers.isDeclaredBy(serviceInterface)).intercept(interceptor);

      for (final Class<?> iface : Types.getInterfacesRecursive(serviceInterface)) {
         builder = builder.implement(iface) //
            .method(ElementMatchers.isDeclaredBy(iface)).intercept(interceptor);
      }

      final Loaded<DefaultServiceProxyAdvice<SERVICE_INTERFACE>> clazz = builder.make().load(DefaultServiceRegistry.class.getClassLoader(),
         ClassLoadingStrategy.Default.WRAPPER);

      /*try {
          clazz.saveIn(new java.io.File("F:\\out"));
      } catch (final java.io.IOException ex) {
          ex.printStackTrace();
      }*/

      final DefaultServiceProxyAdvice<SERVICE_INTERFACE> proxy = Types.newInstance(clazz.getLoaded(), serviceEndpointState, serviceInterface);
      proxy.setProxy(proxy);
      return proxy;
   }
}
