/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Composite<Component>
{
	public class Default<Component> implements Composite<Component>, Serializable
	{
		private static final long serialVersionUID = 1L;

		protected final ArrayList<Component> components = new ArrayList<Component>();

		public Default()
		{
			super();
		}

		public Default(final Collection< ? extends Component> components)
		{
			this.components.addAll(components);
		}

		public Default(final Component... components)
		{
			CollectionUtils.addAll(this.components, components);
		}

		public void addComponent(final Component component)
		{
			components.add(component);
		}

		public boolean hasComponent(final Component component)
		{
			return components.contains(component);
		}

		public boolean removeComponent(final Component component)
		{
			return components.remove(component);
		}
	}

	void addComponent(final Component component);

	boolean removeComponent(final Component component);

	boolean hasComponent(final Component component);
}
