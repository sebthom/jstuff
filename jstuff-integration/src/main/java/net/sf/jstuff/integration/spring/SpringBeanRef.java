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
package net.sf.jstuff.integration.spring;

import java.io.Serializable;

import net.sf.jstuff.core.validation.Args;

/**
 * A serializable reference to a Spring managed bean. Relies on a configured {@link SpringBeanLocator}.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanRef<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static <T> SpringBeanRef<T> of(final Class<T> beanType) {
        return new SpringBeanRef<T>(beanType);
    }

    public static <T> SpringBeanRef<T> of(final String beanName) {
        return new SpringBeanRef<T>(beanName);
    }

    private transient T springBean;

    private final String beanName;

    private final Class<T> beanType;

    private SpringBeanRef(final Class<T> beanType) {
        Args.notNull("beanType", beanType);
        this.beanType = beanType;
        beanName = null;
    }

    private SpringBeanRef(final String beanName) {
        Args.notNull("beanName", beanName);
        this.beanName = beanName;
        beanType = null;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (springBean == null)
            springBean = (T) (beanName == null ? SpringBeanLocator.get().byClass(beanType) : SpringBeanLocator.get().byName(beanName));
        return springBean;
    }
}
