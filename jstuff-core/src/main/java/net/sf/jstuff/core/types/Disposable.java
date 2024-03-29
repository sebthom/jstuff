/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Disposable {

   class Default implements Disposable {
      @Override
      public void dispose() {
      }
   }

   void dispose();
}
