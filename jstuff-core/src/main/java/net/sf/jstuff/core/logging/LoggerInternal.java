/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging;

import java.lang.reflect.Method;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
interface LoggerInternal extends Logger {

   void trace(Method location, String msg);

}
