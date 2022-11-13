package io.eiren.util.collections;

import java.util.List;


public interface RemoveAtSwapList<E> extends List<E> {

	public E removeAtSwap(int i);

	public boolean removeAtSwap(Object object);
}
