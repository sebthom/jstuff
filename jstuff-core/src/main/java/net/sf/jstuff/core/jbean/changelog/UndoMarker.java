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
package net.sf.jstuff.core.jbean.changelog;

import java.util.UUID;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UndoMarker extends PropertyChangeEvent
{
	private static final long serialVersionUID = 1L;

	private final String id = UUID.randomUUID().toString();

	UndoMarker()
	{
		super(null, null);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof UndoMarker)) return false;
		final UndoMarker other = (UndoMarker) obj;
		return id.equals(other.id);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public void undo()
	{
		// do nothing
	}
}
