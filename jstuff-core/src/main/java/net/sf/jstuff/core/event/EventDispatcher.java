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
package net.sf.jstuff.core.event;

import java.util.concurrent.Future;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventDispatcher<EVENT> extends EventListenable<EVENT> {

   Future<Integer> fire(EVENT event);

   void unsubscribeAll();
}
