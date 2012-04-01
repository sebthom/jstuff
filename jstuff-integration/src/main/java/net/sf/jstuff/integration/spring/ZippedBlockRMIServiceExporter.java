/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.integration.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockRMIServiceExporter implements InitializingBean, DisposableBean
{
	private final RmiServiceExporter normalExporter = new RmiServiceExporter();
	private final RmiServiceExporter compressedExporter = new RmiServiceExporter();
	private boolean serviceNameSet = false;

	public ZippedBlockRMIServiceExporter()
	{
		compressedExporter.setRegistryPort(1100);
	}

	/**
	 * {@inheritDoc}
	 */
	public void afterPropertiesSet() throws Exception
	{
		if (!serviceNameSet) setServiceName(normalExporter.getServiceInterface().getSimpleName());

		compressedExporter.setRegistryClientSocketFactory(new ZippedBlockRMISocketFactory());
		compressedExporter.afterPropertiesSet();
		normalExporter.afterPropertiesSet();
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() throws Exception
	{
		compressedExporter.destroy();
		normalExporter.destroy();
	}

	/**
	 * detault is 1100
	 * @param registryPort
	 */
	public void setRegistryCompressedPort(final int registryPort)
	{
		compressedExporter.setRegistryPort(registryPort);
	}

	public void setRegistryHost(final String registryHost)
	{
		compressedExporter.setRegistryHost(registryHost);
		normalExporter.setRegistryHost(registryHost);
	}

	/**
	 * detault is 1099
	 * @param registryPort
	 */
	public void setRegistryPort(final int registryPort)
	{
		normalExporter.setRegistryPort(registryPort);
	}

	public void setService(final Object service)
	{
		compressedExporter.setService(service);
		normalExporter.setService(service);
	}

	public void setServiceInterface(final Class< ? > serviceInterface)
	{
		compressedExporter.setServiceInterface(serviceInterface);
		normalExporter.setServiceInterface(serviceInterface);
	}

	/**
	 * Defaults to the simple class name of the service interface class
	 * @param serviceName
	 */
	public void setServiceName(final String serviceName)
	{
		compressedExporter.setServiceName(serviceName);
		normalExporter.setServiceName(serviceName);
		serviceNameSet = true;
	}
}
