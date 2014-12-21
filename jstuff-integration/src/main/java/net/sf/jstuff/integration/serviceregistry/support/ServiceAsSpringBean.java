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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

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
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceAsSpringBean<T> implements FactoryBean<T>, InitializingBean
{
	private boolean isInitialized = false;

	private ServiceRegistry serviceRegistry;

	/**
	 * @optional by default the fully qualified name of the service interface is used
	 */
	private String serviceEndpointId;
	private Class<T> serviceInterface;
	private T service;

	public synchronized void afterPropertiesSet() throws Exception
	{
		Assert.isFalse(isInitialized, "Already initialized!");
		Assert.notNull(serviceRegistry, "[serviceRegistry] must not be null!");
		Assert.notNull(serviceInterface, "[serviceInterface] must not be null!");
		if (serviceEndpointId == null)
		{
			serviceEndpointId = serviceInterface.getName();
		}
		isInitialized = true;
		service = serviceRegistry.getService(serviceEndpointId, serviceInterface).get();
	}

	public T getObject() throws Exception
	{
		return service;
	}

	public Class<T> getObjectType()
	{
		return serviceInterface;
	}

	public boolean isSingleton()
	{
		return true;
	}

	public synchronized void setServiceEndpointId(final String serviceEndpointId)
	{
		Args.notNull("serviceEndpointId", serviceEndpointId);
		Assert.isFalse(isInitialized, "Already initialized!");

		this.serviceEndpointId = serviceEndpointId;
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
