/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface Invocable<ReturnType, ArgumentType, ExceptionType extends Exception> {
   ReturnType invoke(ArgumentType arg) throws ExceptionType;
}
