/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.support;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;

/**
 * Helper class to expose services registered with the {@link ServiceRegistry} as Spring beans.
 * <p>
 * I.e. the service will be looked up in the given serviceRegistry made available for injection into other Spring-managed beans.
 *
 * <pre>
 * {@code
 * <bean id="myServiceFromTheRegistry" class="net.sf.jstuff.integration.serviceregistry.support.ServiceAsSpringBean">
 *     <property name="serviceRegistry" ref="mySpringManagedServiceRegistry" />
 *     <!-- if serviceEndpointId is not specified the fully qualified class name of the interface will be used -->
 *     <property name="serviceEndpointId" value="com.example.MyService" />
 *     <property name="serviceInterface" value="com.example.MyService" />
 * </bean>
 * }
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceAsSpringBean<T> implements FactoryBean<T>, InitializingBean {

   private ServiceRegistry serviceRegistry = lateNonNull();

   /**
    * @optional by default the fully qualified name of the service interface is used
    */
   private @Nullable String serviceEndpointId;
   private Class<T> serviceInterface = lateNonNull();
   private T service = lateNonNull();

   @Override
   public synchronized void afterPropertiesSet() throws Exception {
      Assert.isNull(service, "Already initialized!");
      Assert.notNull(serviceRegistry, "[serviceRegistry] must not be null!");
      Assert.notNull(serviceInterface, "[serviceInterface] must not be null!");

      service = serviceRegistry.getService(//
         serviceEndpointId != null ? serviceEndpointId : serviceInterface.getName(), //
         serviceInterface //
      ).get();
   }

   @Override
   public T getObject() throws Exception {
      return service;
   }

   @Override
   public @Nullable Class<T> getObjectType() {
      return serviceInterface;
   }

   @Override
   public boolean isSingleton() {
      return true;
   }

   public synchronized void setServiceEndpointId(final String serviceEndpointId) {
      Args.notNull("serviceEndpointId", serviceEndpointId);
      Assert.isNull(service, "Already initialized!");

      this.serviceEndpointId = serviceEndpointId;
   }

   public synchronized void setServiceInterface(final Class<T> serviceInterface) {
      Args.notNull("serviceInterface", serviceInterface);
      Assert.isNull(service, "Already initialized!");
      Assert.isTrue(serviceInterface.isInterface(), "[serviceInterface] must be an interface but is " + serviceInterface);

      this.serviceInterface = serviceInterface;
   }

   public synchronized void setServiceRegistry(final ServiceRegistry serviceRegistry) {
      Args.notNull("serviceRegistry", serviceRegistry);
      Assert.isNull(service, "Already initialized!");

      this.serviceRegistry = serviceRegistry;
   }
}
