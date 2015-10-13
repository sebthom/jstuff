/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import junit.framework.TestCase;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderTest extends TestCase
{
	public static class EntityA
	{
		public interface EntityABuilder<T extends EntityA> extends Builder<T>
		{
			EntityABuilder<T> propertyA(final String value);

			EntityABuilder<T> propertyB(final int value);
		}

		@SuppressWarnings("unchecked")
		public static EntityABuilder< ? extends EntityA> builder()
		{
			return (EntityABuilder< ? extends EntityA>) BuilderFactory.of(EntityABuilder.class, EntityA.class).create();
		}

		private String propertyA;
		protected Integer propertyB = -1;

		@OnPostBuild
		private void onInitialized()
		{
			Args.notNull("propertyA", propertyA);
			Args.notNull("propertyB", propertyB);
			Args.notNegative("propertyB", propertyB);
		}
	}

	public static class EntityB extends EntityA
	{
		public interface EntityBBuilder<T extends EntityB> extends EntityABuilder<T>
		{
			EntityBBuilder<T> propertyA(final String value);

			EntityBBuilder<T> propertyB(final int value);

			EntityBBuilder<T> propertyC(final Long value);

			EntityBBuilder<T> propertyD(final String value);
		}

		@SuppressWarnings("unchecked")
		public static EntityBBuilder< ? extends EntityB> builder()
		{
			return (EntityBBuilder< ? extends EntityB>) BuilderFactory.of(EntityBBuilder.class, EntityB.class).create();
		}

		public long propertyC = -1;
		private String propertyD;

		@OnPostBuild
		private void onInitialized()
		{
			Args.notNegative("propertyC", propertyC);
			Args.notNull("propertyD", propertyD);
			if (!propertyD.endsWith("_setWithSetter")) throw new IllegalArgumentException("propertyD not set via setter");
		}

		public void setPropertyD(final String propertyD)
		{
			this.propertyD = propertyD + "_setWithSetter";
		}
	}

	public void testBuilder()
	{
		EntityA.builder().propertyA("foo").propertyB(1).build();

		try
		{
			EntityA.builder().build();
			fail();
		}
		catch (final Exception ex)
		{}

		try
		{
			EntityA.builder().propertyA("foo").build();
			fail();
		}
		catch (final Exception ex)
		{}

		try
		{
			EntityA.builder().propertyA("foo").propertyB(-1).build();
			fail();
		}
		catch (final Exception ex)
		{}

		EntityB.builder().propertyA("foo").propertyB(1).propertyC(3L).propertyD("bar").build();

		try
		{
			EntityB.builder().build();
			fail();
		}
		catch (final Exception ex)
		{}

		try
		{
			EntityB.builder().propertyC(3L).propertyD("bar").build();
			fail();
		}
		catch (final Exception ex)
		{}
		try
		{
			EntityB.builder().propertyA("foo").propertyB(1).propertyC(-1L).propertyD("bar").build();
			fail();
		}
		catch (final Exception ex)
		{}
	}
}
