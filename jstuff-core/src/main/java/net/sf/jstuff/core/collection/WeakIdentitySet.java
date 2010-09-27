/*
 *  Based on
 *  
 *  http://www.koders.com/java/fid9ABEE1C55A73E0899A1AD7B6D5532ADA4925820A.aspx
 *  
 *  Copyright 2005 Christian Essl
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.sf.jstuff.core.collection;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author Christian Essl
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentitySet<E>
{
	/**
	 * The entries in this hash table extend WeakReference, using its main ref
	 * field as the key. 
	 */
	private final static class Entry<E> extends WeakReference<E>
	{
		protected int hash;
		protected Entry<E> next;

		/**
		 * Create new entry.
		 */
		protected Entry(final E key, final ReferenceQueue<E> queue, final int hash, final Entry<E> next)
		{
			super(key, queue);
			this.hash = hash;
			this.next = next;
		}

		@Override
		public boolean equals(final Object o)
		{
			if (!(o instanceof Entry< ? >)) return false;

			@SuppressWarnings("unchecked")
			final Entry<E> e = (Entry<E>) o;
			final E k1 = get();
			final E k2 = e.get();
			if (k1 == k2) return true;
			return false;
		}

		@Override
		public int hashCode()
		{
			final E k = get();
			return k == null ? 0 : k.hashCode();
		}
	}

	private static int hash(final Object x)
	{
		final int h = System.identityHashCode(x);
		// Multiply by -127, and left-shift to use least bit as part of hash
		return (h << 1) - (h << 8);
	}

	private static int indexFor(final int h, final int length)
	{
		return h & length - 1;
	}

	/**
	* Length MUST Always be a power of two.
	 */
	private Entry<E>[] table;

	private int size;

	private int threshold;

	private final float loadFactor;

	private final ReferenceQueue<E> queue = new ReferenceQueue<E>();

	/**
	 * Constructs a new, empty <tt>WeakIdentitySet</tt> with
	 * the default initial capacity (16) and the default load factor (0.75).
	 */
	public WeakIdentitySet()
	{
		this(16, 0.75f);
	}

	/**
	 * Constructs a new, empty <tt>WeakIdentitySet</tt> with
	 * the given initial capacity and the default load factor (0.75).
	 */
	public WeakIdentitySet(final int initialCapacity)
	{
		this(initialCapacity, 0.75f);
	}

	/**
	 * Constructs a new, empty <tt>WeakIdentitySet</tt> with
	 * the given initial capacity and the given load factor (0.75).
	 */
	@SuppressWarnings("unchecked")
	public WeakIdentitySet(final int initialCapacity, final float loadFactor)
	{
		threshold = initialCapacity;
		table = new Entry[initialCapacity];
		this.loadFactor = loadFactor;
	}

	public synchronized void add(final E o)
	{
		final int h = hash(o);
		final Entry<E>[] tab = getTable();
		final int i = indexFor(h, tab.length);

		for (Entry<E> e = tab[i]; e != null; e = e.next)
			if (h == e.hash && o == e.get()) return;

		tab[i] = new Entry<E>(o, queue, h, tab[i]);
		if (++size >= threshold) resize(tab.length * 2);
		return;
	}

	public synchronized boolean contains(final Object key)
	{
		return getEntry(key) != null;
	}

	private void expungeStaleEntries()
	{
		Reference< ? extends E> r;
		while ((r = queue.poll()) != null)
		{
			@SuppressWarnings("unchecked")
			final Entry<E> e = (Entry<E>) r;
			final int h = e.hash;
			final int i = indexFor(h, table.length);

			Entry<E> prev = table[i];
			Entry<E> p = prev;
			while (p != null)
			{
				final Entry<E> next = p.next;
				if (p == e)
				{
					if (prev == e)
						table[i] = next;
					else
						prev.next = next;
					e.next = null; // Help GC
					size--;
					break;
				}
				prev = p;
				p = next;
			}
		}
	}

	private Entry<E> getEntry(final Object k)
	{
		final int h = hash(k);
		final Entry<E>[] tab = getTable();
		final int index = indexFor(h, tab.length);
		Entry<E> e = tab[index];
		while (e != null && !(e.hash == h && k == e.get()))
			e = e.next;
		return e;
	}

	private Entry<E>[] getTable()
	{
		expungeStaleEntries();
		return table;
	}

	public void remove(final Object k)
	{
		final int h = hash(k);
		final Entry<E>[] tab = getTable();
		final int i = indexFor(h, tab.length);
		Entry<E> prev = tab[i];
		Entry<E> e = prev;

		while (e != null)
		{
			final Entry<E> next = e.next;
			if (h == e.hash && k == e.get())
			{
				size--;
				if (prev == e)
					tab[i] = next;
				else
					prev.next = next;
				return;
			}
			prev = e;
			e = next;
		}
	}

	private void resize(final int newCapacity)
	{
		final Entry<E>[] oldTable = getTable();
		final int oldCapacity = oldTable.length;

		// check if needed
		if (size < threshold || oldCapacity > newCapacity) return;

		@SuppressWarnings("unchecked")
		final Entry<E>[] newTable = new Entry[newCapacity];

		transferEntries(oldTable, newTable);
		table = newTable;

		/*
		 * If ignoring null elements and processing ref queue caused massive
		 * shrinkage, then restore old table.  This should be rare, but avoids
		 * unbounded expansion of garbage-filled tables.
		 */
		if (size >= threshold / 2)
			threshold = (int) (newCapacity * loadFactor);
		else
		{
			expungeStaleEntries();
			transferEntries(newTable, oldTable);
			table = oldTable;
		}
	}

	public synchronized int size()
	{
		if (size == 0) return 0;
		expungeStaleEntries();
		return size;
	}

	private void transferEntries(final Entry<E>[] src, final Entry<E>[] dest)
	{
		for (int j = 0; j < src.length; ++j)
		{
			Entry<E> e = src[j];
			src[j] = null;
			while (e != null)
			{
				final Entry<E> next = e.next;
				final Object key = e.get();
				if (key == null)
				{
					e.next = null; // Help GC
					size--;
				}
				else
				{
					final int i = indexFor(e.hash, dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}
}