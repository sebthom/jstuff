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
package net.sf.jstuff.integration.persistence;

import net.sf.jstuff.core.Identifiable;

@javax.persistence.Entity
public class Entity extends Identifiable.Default<Integer>
{
	private static final long serialVersionUID = 1L;

	@javax.persistence.Transient
	private final String _hashCodeTrackingId;

	private Integer id;

	public Entity()
	{
		_hashCodeTrackingId = IdentifiableHashCodeManager.onEntityInstantiated(this);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Entity other = (Entity) obj;
		if (id == null)
		{
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public Integer getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		return IdentifiableHashCodeManager.getHashCode(this, _hashCodeTrackingId);
	}

	@Override
	public void setId(final Integer id)
	{
		if (id.equals(this.id)) return;
		if (this.id != null) throw new IllegalStateException("Id reassignment not allowed");
		this.id = id;

		IdentifiableHashCodeManager.onIdSet(this, _hashCodeTrackingId);
	}

	public Entity setLabel(final String label)
	{
		return this;
	}
}
