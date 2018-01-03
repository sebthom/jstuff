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
package net.sf.jstuff.core.reflection;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StackTrace {

    private static final class CallerResolver extends SecurityManager {
        private static final CallerResolver INSTANCE = new CallerResolver();

        @Override
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

    private static final String CLASSNAME = StackTrace.class.getName();

    public static Class<?> getCallerClass() {
        final String callerClassName = getCallerClassName();

        final Class<?>[] stack = CallerResolver.INSTANCE.getClassContext();
        for (final Class<?> curr : stack)
            if (callerClassName.equals(curr.getName()))
                return curr;
        throw new AssertionError("should never be reached.");
    }

    /**
     * @return class that called the <code>calledClassName</code>
     */
    public static Class<?> getCallerClass(final Class<?> calledClass) {
        Args.notNull("calledClass", calledClass);

        return getCallerClass(calledClass.getName());
    }

    /**
     * @return class that called the <code>calledClassName</code>
     */
    public static Class<?> getCallerClass(final String calledClassName) {
        Args.notNull("calledClassName", calledClassName);

        final Class<?>[] stack = CallerResolver.INSTANCE.getClassContext();
        boolean foundInStack = false;
        for (final Class<?> curr : stack)
            if (calledClassName.equals(curr.getName())) {
                foundInStack = true;
            } else if (foundInStack)
                return curr;
        return null;
    }

    public static String getCallerClassName() {
        final StackTraceElement ste = getCallerStackTraceElement();
        return ste.getClassName();
    }

    public static String getCallerClassSimpleName() {
        final StackTraceElement ste = getCallerStackTraceElement();
        return Strings.substringAfterLast(ste.getClassName(), ".");
    }

    public static String getCallerFileName() {
        final StackTraceElement ste = getCallerStackTraceElement();
        return ste.getFileName();
    }

    public static int getCallerLineNumber() {
        final StackTraceElement ste = getCallerStackTraceElement();
        return ste.getLineNumber();
    }

    public static String getCallerMethodName() {
        final StackTraceElement ste = getCallerStackTraceElement();
        return ste.getMethodName();
    }

    public static StackTraceElement getCallerStackTraceElement() {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean stackTraceClassFound = false;
        for (int i = 0, l = stack.length; i < l; i++) {
            final StackTraceElement elem = stack[i];
            if (CLASSNAME.equals(elem.getClassName())) {
                stackTraceClassFound = true;
            } else {
                if (stackTraceClassFound)
                    return stack[i + 1];
            }
        }
        throw new AssertionError("should never be reached.");
    }

    public static StackTraceElement getCallerStackTraceElement(final Class<?> calledClass) {
        Args.notNull("calledClass", calledClass);

        return getCallerStackTraceElement(calledClass.getName());
    }

    public static StackTraceElement getCallerStackTraceElement(final String calledClassName) {
        Args.notNull("calledClassName", calledClassName);

        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean foundInStack = false;
        for (final StackTraceElement curr : stack)
            if (calledClassName.equals(curr.getClassName())) {
                foundInStack = true;
            } else if (foundInStack)
                return curr;
        return null;
    }

    public static String getThisFileName() {
        final StackTraceElement ste = getThisStackTraceElement();
        return ste.getFileName();
    }

    public static int getThisLineNumber() {
        final StackTraceElement ste = getThisStackTraceElement();
        return ste.getLineNumber();
    }

    public static String getThisMethodName() {
        final StackTraceElement ste = getThisStackTraceElement();
        return ste.getMethodName();
    }

    public static StackTraceElement getThisStackTraceElement() {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean stackTraceClassFound = false;
        for (final StackTraceElement elem : stack) {
            if (CLASSNAME.equals(elem.getClassName())) {
                stackTraceClassFound = true;
            } else {
                if (stackTraceClassFound)
                    return elem;
            }
        }
        throw new AssertionError("should never be reached.");
    }

    /**
     * @return the given exception
     */
    public static <T extends Throwable> T removeFirstStackTraceElement(final T exception) {
        Args.notNull("exception", exception);

        final StackTraceElement[] stack = exception.getStackTrace();
        if (stack != null && stack.length > 0) {
            final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
            System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
            exception.setStackTrace(newStack);
        }
        return exception;
    }

}