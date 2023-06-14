package io.eiren.util.collections;

import java.util.Iterator;


/**
 * {@link Iterator} that can be reset and iterated from the start by using
 * {@link #reset()}
 * 
 * @author Rena
 *
 * @param <E>
 */
public interface ResettableIterator<E> extends Iterator<E> {

	void reset();
}
