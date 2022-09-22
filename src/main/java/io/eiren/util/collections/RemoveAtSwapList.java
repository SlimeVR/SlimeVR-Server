package io.eiren.util.collections;

import java.util.List;


public interface RemoveAtSwapList<E> extends List<E> {

	E removeAtSwap(int i);

	boolean removeAtSwap(Object object);
}
