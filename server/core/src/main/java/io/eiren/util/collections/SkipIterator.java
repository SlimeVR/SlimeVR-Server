package io.eiren.util.collections;

import java.util.Iterator;


/**
 * {@link Iterator} that can return null on {@link #next()} or can lie on
 * {@link #hasNext()}. It is <b>not thread-secure!</b>
 * 
 * @param <E> the type of elements returned by this iterator
 */
public interface SkipIterator<E> extends Iterator<E> {

	@Override
	E next();
}
