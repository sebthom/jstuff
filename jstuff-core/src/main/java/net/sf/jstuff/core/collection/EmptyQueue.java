package net.sf.jstuff.core.collection;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

public class EmptyQueue<E> extends AbstractQueue<E> implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final Queue< ? > INSTANCE = new EmptyQueue<Object>();

	@SuppressWarnings("unchecked")
	public static final <T> EmptyQueue<T> get()
	{
		return (EmptyQueue<T>) INSTANCE;
	}

	private EmptyQueue()
	{
		super();
	}

	@Override
	public void clear()
	{}

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public Iterator<E> iterator()
	{
		return Collections.<E> emptySet().iterator();
	}

	public boolean offer(final E o)
	{
		return false;
	}

	public E poll()
	{
		return null;
	}

	public E peek()
	{
		return null;
	}

	@SuppressWarnings({"static-method", "unused"})
	private Object readResolve() throws ObjectStreamException
	{
		return INSTANCE;
	}
}