/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import java.lang.reflect.Method;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
interface LoggerInternal extends Logger {

   void trace(Method location, String msg);

}
