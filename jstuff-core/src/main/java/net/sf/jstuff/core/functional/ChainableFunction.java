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
package net.sf.jstuff.core.functional;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ChainableFunction<In, Out> extends Function<In, Out>
{
	<NextOut> ChainableFunction<In, NextOut> and(Function< ? super Out, NextOut> next);
}