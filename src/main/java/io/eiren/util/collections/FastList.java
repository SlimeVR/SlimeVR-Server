package io.eiren.util.collections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;


@SuppressWarnings("unchecked")
public class FastList<E> extends AbstractList<E>
	implements RandomAccess, Cloneable, RemoveAtSwapList<E> {

	private static final Object[] emptyArray = new Object[0];

	public static final int MAX_ARRAY_SIZE = 2147483639;

	protected int size = 0;
	protected Object[] array;

	public FastList(int capacity) {
		array = capacity == 0 ? emptyArray : new Object[capacity];
	}

	public FastList() {
		this(5);
	}

	public FastList(Collection<E> source) {
		this(source.size());
		addAll(source);
	}

	public FastList(FastList<E> source) {
		this(source.size);
		addAllInternal(0, source.array, source.size);
	}

	public FastList(E[] source) {
		this(source.length);
		addAll(source);
	}

	public FastList(E source) {
		this();
		add(source);
	}

	private FastList(Object[] arr, int size) {
		this(size);
		System.arraycopy(arr, 0, array, 0, size);
		this.size = size;
	}

	private FastList(boolean f) {
	}

	public static <E> FastList<E> reuseArray(E[] source) {
		FastList<E> list = new FastList<>(true);
		list.array = source;
		list.size = source.length;
		return list;
	}

	private void checkBounds(int index) {
		if (index < 0 || index >= size)
			throw new ArrayIndexOutOfBoundsException(
				new StringBuilder("Index: ")
					.append(index)
					.append(", size: ")
					.append(size)
					.toString()
			);
	}

	public void ensureCapacity(int numToFit) {
		if (array.length < size + numToFit)
			grow(numToFit + size);
	}

	private void grow(int i) {
		int j = array.length;
		int k = j + (j >> 1);
		if (k - i < 0)
			k = i;
		if (k - 2147483639 > 0)
			k = hugeCapacity(i);
		array = Arrays.copyOf(array, k);
	}

	private static int hugeCapacity(int i) {
		if (i < 0)
			throw new OutOfMemoryError("Huge capacity negative: " + i);
		else
			return i <= MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : 2147483647;
	}

	public void copyInto(Object[] anArray) {
		System.arraycopy(array, 0, anArray, 0, size);
	}

	@Override
	public E get(int index) {
		checkBounds(index);
		return (E) array[index];
	}

	public E unsafeGet(int index) {
		return (E) array[index];
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int indexOf(Object obj) {
		for (int j = 0; j < size; j++)
			if (obj == array[j])
				return j;
		return -1;
	}

	@Override
	public int lastIndexOf(Object obj) {
		for (int j = size - 1; j >= 0; j--)
			if (obj == array[j])
				return j;
		return -1;
	}

	@Override
	public boolean contains(Object obj) {
		return indexOf(obj) >= 0;
	}

	public void trimToSize() {
		int i = array.length;
		if (size < i)
			array = Arrays.copyOf(array, size);
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(array, size);
	}

	@Override
	public <T> T[] toArray(T[] aobj) {
		if (aobj.length < size)
			return (T[]) Arrays.copyOf(array, size, aobj.getClass());
		System.arraycopy(array, 0, aobj, 0, size);
		if (aobj.length > size)
			aobj[size] = null;
		return aobj;
	}

	@Override
	public boolean add(E e) {
		ensureCapacity(1);
		array[size++] = e;
		return true;
	}

	@Override
	public E remove(int i) {
		checkBounds(i);
		E obj = (E) array[i];
		removeInternal(i);
		return obj;
	}

	@Override
	public boolean remove(Object obj) {
		for (int j = 0; j < size; j++)
			if (obj == array[j]) {
				removeInternal(j);
				return true;
			}
		return false;
	}

	public boolean removeAll(Object[] toRemove) {
		boolean removed = false;
		for (int i = toRemove.length - 1; i >= 0; --i) {
			int index = indexOf(toRemove[i]);
			if (index != -1) {
				removeInternal(index);
				removed = true;
			}
		}
		return removed;
	}

	protected void removeInternal(int i) {
		int j = size - i - 1;
		if (j > 0)
			System.arraycopy(array, i + 1, array, i, j);
		array[--size] = null;
	}

	public void unsafeRemove(int i) {
		removeInternal(i);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, false);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, true);
	}

	private boolean batchRemove(Collection<?> c, boolean complement) {
		final Object[] elementData = this.array;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++)
				if (c.contains(elementData[r]) == complement)
					elementData[w++] = elementData[r];
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.
			if (r != size) {
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				for (int i = w; i < size; i++)
					elementData[i] = null;
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		for (int i = 0; i < size; i++)
			array[i] = null;
		size = 0;
	}

	public void fakeClear() {
		size = 0;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return addAll(size, collection);
	}

	public void addAll(E[] arr) {
		addAllInternal(size, arr, arr.length);
	}

	public void addAll(E[] arr, int limit) {
		addAllInternal(size, arr, limit);
	}

	public void addAll(int index, E[] arr) {
		addAllInternal(index, arr, arr.length);
	}

	public void addAll(int index, E[] arr, int limit) {
		addAllInternal(index, arr, limit);
	}

	private void addAllInternal(int index, Object[] arr, int limit) {
		if (limit > arr.length)
			limit = arr.length;
		if (limit == 1) {
			add(index, (E) arr[0]);
		} else if (limit > 0) {
			if (index >= size) {
				ensureCapacity(size - index + limit);
				System.arraycopy(arr, 0, array, index, limit);
				size = index + limit;
			} else {
				if (array.length < size + limit) {
					Object[] newArray = new Object[size + limit];
					System.arraycopy(array, 0, newArray, 0, index);
					System.arraycopy(arr, 0, newArray, index, limit);
					System.arraycopy(array, index, newArray, index + limit, size - index);
					array = newArray;
				} else {
					System.arraycopy(array, index, array, index + 1, size - index);
					System.arraycopy(arr, 0, array, index, limit);
				}
				size += limit;
			}
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		if (collection.size() > 0) {
			if (collection instanceof FastList) {
				addAllInternal(
					index,
					((FastList<? extends E>) collection).array,
					collection.size()
				);
			} else if (collection instanceof RandomAccess) {
				Object[] arr = collection.toArray(new Object[collection.size()]);
				addAllInternal(index, arr, arr.length);
			} else {
				if (index >= size) {
					ensureCapacity(size - index + collection.size());
					Iterator<? extends E> iterator = collection.iterator();
					int i = index;
					while (iterator.hasNext())
						array[i++] = iterator.next();
					size = index + collection.size();
				} else {
					if (array.length < size + collection.size()) {
						Object[] newArray = new Object[size + collection.size()];
						System.arraycopy(array, 0, newArray, 0, index);
						Iterator<? extends E> iterator = collection.iterator();
						int i = index;
						while (iterator.hasNext())
							newArray[i++] = iterator.next();
						System
							.arraycopy(
								array,
								index,
								newArray,
								index + collection.size(),
								size - index
							);
						array = newArray;
					} else {
						System.arraycopy(array, index, array, index + 1, size - index);
						for (E e : collection)
							array[index++] = e;
					}
					size += collection.size();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void add(int index, E element) {
		if (index >= size) {
			ensureCapacity(size - index + 1);
			size = index + 1;
			array[index] = element;
		} else {
			if (array.length < size + 1) {
				Object[] newArray = new Object[size + 1];
				System.arraycopy(array, 0, newArray, 0, index);
				newArray[index] = element;
				System.arraycopy(array, index, newArray, index + 1, size - index);
				array = newArray;
			} else {
				System.arraycopy(array, index, array, index + 1, size - index);
				array[index] = element;
			}
			size++;
		}
	}

	@Override
	public E set(int index, E element) {
		checkBounds(index);
		E oldValue = (E) array[index];
		array[index] = element;
		return oldValue;
	}

	@Override
	public FastList<E> clone() {
		return new FastList<>(array, size);
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		final int expectedModCount = modCount;
		final E[] elementData = (E[]) this.array;
		final int size = this.size;
		for (int i = 0; modCount == expectedModCount && i < size; i++) {
			action.accept(elementData[i]);
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public E removeAtSwap(int i) {
		checkBounds(i);
		E obj = (E) array[i];
		removeAtSwapInternal(i);
		return obj;
	}

	@Override
	public boolean removeAtSwap(Object obj) {
		for (int j = 0; j < size; j++)
			if (obj == array[j]) {
				removeAtSwapInternal(j);
				return true;
			}
		return false;
	}

	protected void removeAtSwapInternal(int i) {
		int j = size - i - 1;
		if (j > 0)
			array[i] = array[size - 1];
		array[--size] = null;
	}

	@Override
	public void removeRange(int i, int toIndex) {
		checkBounds(i);
		checkBounds(toIndex);
		int j = size - toIndex - 1;
		if (j > 0)
			System.arraycopy(array, toIndex + 1, array, i, j);
		size -= (toIndex - i + 1);
		Arrays.fill(array, i, toIndex, null);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		for (int i = 0; i < size; ++i)
			set(i, operator.apply(get(i)));
	}

	@Override
	public void sort(Comparator<? super E> c) {
		Arrays.sort((E[]) array, 0, size, c);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < size; ++i) {
			Object o = array[i];
			hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
		}
		return hashCode;
	}

	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(array, 0, size, Spliterator.ORDERED);
	}

	/**
	 * Special comodification iterator. <b>Use with caution.</b>
	 * <p>
	 * <i>To get element type correctly assign result to reference type
	 * {@code FastList<T>.SkipFastListIterator}</i>
	 * 
	 * @return skip iterator to iterate this list in thread-safe manner
	 */
	public SkipFastListIterator skipIterator() {
		return new SkipFastListIterator();
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		// figure out which elements are to be removed
		// any exception thrown from the filter predicate at this stage
		// will leave the collection unmodified
		int removeCount = 0;
		final BitSet removeSet = new BitSet(size);
		final int expectedModCount = modCount;
		final int size = this.size;
		for (int i = 0; modCount == expectedModCount && i < size; i++) {
			final E element = (E) array[i];
			if (filter.test(element)) {
				removeSet.set(i);
				removeCount++;
			}
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}

		// shift surviving elements left over the spaces left by removed
		// elements
		final boolean anyToRemove = removeCount > 0;
		if (anyToRemove) {
			final int newSize = size - removeCount;
			for (int i = 0, j = 0; (i < size) && (j < newSize); i++, j++) {
				i = removeSet.nextClearBit(i);
				array[j] = array[i];
			}
			for (int k = newSize; k < size; k++) {
				array[k] = null; // Let gc do its work
			}
			this.size = newSize;
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			modCount++;
		}

		return anyToRemove;
	}

	public class SkipFastListIterator implements ResettableIterator<E>, SkipIterator<E> {

		public int position;

		@Override
		public boolean hasNext() {
			return position < size;
		}

		@Override
		public E next() {
			Object[] arr = array;
			if (arr.length > position) {
				return (E) arr[position++];
			}
			position++; // Increase position so hasNext() never loops infinitely
			return null;
		}

		@Override
		public void reset() {
			position = 0;
		}
	}
}
