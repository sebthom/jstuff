/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.serviceregistry.support;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Helper class to expose Spring managed beans as services to the {@link ServiceRegistry}.
 * <p>
 * I.e. a Spring managed bean will be registered as service under the given serviceEndpointId in a {@link ServiceRegistry}.
 *
 * <pre>
 * {@code
 * <bean class="net.sf.jstuff.integration.serviceregistry.support.SpringBeanAsService">
 *     <property name="serviceRegistry" ref="mySpringManagedServiceRegistry" />
 *     <!-- if serviceEndpointId is not specified the fully qualified class name of the interface will be used -->
 *     <property name="serviceEndpointId" value="com.example.MySpringManagedService" />
 *     <property name="serviceInterface" value="com.example.MySpringManagedService" />
 *     <property name="service" ref="mySpringManagedService" />
 * </bean>
 * }
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanAsService<T> implements InitializingBean, DisposableBean
{
	private boolean isInitialized = false;

	private ServiceRegistry serviceRegistry;

	/**
	 * @optional by default the fully qualified name of the service interface is used
	 */
	private String serviceEndpointId;
	private Class<T> serviceInterface;
	private T springBean;

	public synchronized void afterPropertiesSet() throws Exception
	{
		Assert.isFalse(isInitialized, "Already initialized!");
		Assert.notNull(serviceRegistry, "[serviceRegistry] must not be null!");
		Assert.notNull(serviceInterface, "[serviceInterface] must not be null!");
		Assert.notNull(springBean, "[service] must not be null!");
		if (serviceEndpointId == null)
		{
			serviceEndpointId = serviceInterface.getName();
		}
		isInitialized = true;

		serviceRegistry.addService(serviceEndpointId, serviceInterface, springBean);
	}

	public synchronized void destroy() throws Exception
	{
		if (!isInitialized) return;
		serviceRegistry.removeService(serviceEndpointId, springBean);
	}

	public synchronized void setService(final T service)
	{
		Args.notNull("service", service);
		Assert.isFalse(isInitialized, "Already initialized!");

		this.springBean = service;
	}

	public synchronized void setServiceInterface(final Class<T> serviceInterface)
	{
		Args.notNull("serviceInterface", serviceInterface);
		Assert.isFalse(isInitialized, "Already initialized!");
		Assert.isTrue(serviceInterface.isInterface(), "[serviceInterface] must be an interface but is " + serviceInterface);

		this.serviceInterface = serviceInterface;
	}

	public synchronized void setServiceRegistry(final ServiceRegistry serviceRegistry)
	{
		Args.notNull("serviceRegistry", serviceRegistry);
		Assert.isFalse(isInitialized, "Already initialized!");

		this.serviceRegistry = serviceRegistry;
	}
}
