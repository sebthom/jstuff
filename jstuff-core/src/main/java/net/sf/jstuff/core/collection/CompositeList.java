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
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.Composite;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeList<V> extends AbstractList<V> implements Composite<List<? extends V>>, Serializable {

    private static final long serialVersionUID = 1L;

    public static <V> CompositeList<V> of(final Collection<List<? extends V>> components) {
        return new CompositeList<V>(components);
    }

    public static <V> CompositeList<V> of(final List<? extends V>... components) {
        return new CompositeList<V>(components);
    }

    private final Collection<List<? extends V>> components = new ArrayList<List<? extends V>>();

    public CompositeList() {
        super();
    }

    public CompositeList(final Collection<List<? extends V>> components) {
        this.components.addAll(components);
    }

    public CompositeList(final List<? extends V>... components) {
        CollectionUtils.addAll(this.components, components);
    }

    public void addComponent(final List<? extends V> component) {
        this.components.add(component);
    }

    @Override
    public V get(final int index) {
        int totalSizeOfCheckedLists = 0;
        for (final List<? extends V> list : components) {
            final int currentListIndex = index - totalSizeOfCheckedLists;
            final int currentListSize = list.size();
            if (currentListIndex >= currentListSize) {
                totalSizeOfCheckedLists += currentListSize;
                continue;
            }
            return list.get(currentListIndex);
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSizeOfCheckedLists);
    }

    public boolean hasComponent(final List<? extends V> component) {
        return components.contains(component);
    }

    public boolean isCompositeModifiable() {
        return true;
    }

    public boolean removeComponent(final List<? extends V> component) {
        return components.remove(component);
    }

    @Override
    public int size() {
        int size = 0;
        for (final List<? extends V> list : components) {
            size += list.size();
        }
        return size;
    }
}
