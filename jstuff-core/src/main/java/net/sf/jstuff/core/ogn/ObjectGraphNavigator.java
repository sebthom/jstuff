/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ogn;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ObjectGraphNavigator {
   /**
    * Navigates through the object graph starting at <code>root</code> object.
    *
    * @param root the root object to start the navigation from
    * @param path the object navigation path relative to the root object. The path format is implementation specific.
    * @return the result of the navigation operation. <code>null</code> is returned if the target could not be determined, e.g. because of null values in the
    *         path.
    * @throws IllegalArgumentException if the given path is invalid, e.g. because of non-existing fields/properties named in the path.
    */
   @Nullable
   ObjectGraphNavigationResult navigateTo(Object root, String path);

   @Nullable
   <T> T getValueAt(Object root, String path);
}
