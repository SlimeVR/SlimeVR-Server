package io.eiren.util.collections;

import java.util.Collection;


/**
 * FastList that performs Remove-At-Swap on stanard remove() operations.
 * 
 * <p>
 * Remove operations breaks ordering of this list
 * 
 * @author Rena
 *
 * @param <E>
 */
public class RemoveAtSwapFastList<E> extends FastList<E> {

	public RemoveAtSwapFastList(int capacity) {
		super(capacity);
	}

	public RemoveAtSwapFastList() {
	}

	public RemoveAtSwapFastList(Collection<E> source) {
		super(source);
	}

	public RemoveAtSwapFastList(E[] source) {
		super(source);
	}

	public RemoveAtSwapFastList(E source) {
		super(source);
	}

	@Override
	protected void removeInternal(int i) {
		super.removeAtSwapInternal(i);
	}
}
