package io.eiren.util;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class Util {

	public static void close(Object r) {
		try {
			if (r != null) {
				if (r instanceof Closeable)
					((Closeable) r).close();
				else if (r instanceof AutoCloseable)
					((AutoCloseable) r).close();
			}
		} catch (Exception ignored) {}
	}

	public static void close(Object r1, Object r2) {
		close(r1);
		close(r2);
	}

	public static void close(Object... r) {
		for (Object o : r)
			try {
				if (o != null) {
					if (o instanceof Closeable)
						((Closeable) o).close();
					else if (o instanceof AutoCloseable)
						((AutoCloseable) o).close();
				}
			} catch (Exception ignored) {}
	}

	public static void close(AutoCloseable... r) {
		for (AutoCloseable autoCloseable : r)
			try {
				if (autoCloseable != null)
					autoCloseable.close();
			} catch (Exception ignored) {}
	}

	public static void close(Closeable... r) {
		for (Closeable closeable : r)
			try {
				if (closeable != null)
					closeable.close();
			} catch (Exception ignored) {}
	}

	/**
	 * <p>
	 * Performs a deep toString of provided object. It shows content of arrays,
	 * collections and maps (trove not supported yet).
	 * </p>
	 * <p>
	 * <b>Highly ineffective, use only for debug.</b>
	 * </p>
	 * 
	 * @param object
	 * @return
	 */
	public static String toString(Object object) {
		if (object == null)
			return "null";
		StringBuilder buf = new StringBuilder();
		elementToString(object, buf, new HashSet<>());
		return buf.toString();
	}

	private static void deepToString(Map<Object, Object> m, StringBuilder buf, Set<Object> dejaVu) {
		if (m == null) {
			buf.append("null");
			return;
		}
		if (m.size() == 0) {
			buf.append("{}");
			return;
		}
		dejaVu.add(m);
		buf.append('{');
		Iterator<Entry<Object, Object>> iterator = m.entrySet().iterator();
		boolean has = false;
		while (iterator.hasNext()) {
			if (has)
				buf.append(',');
			Entry<Object, Object> e = iterator.next();
			elementToString(e.getKey(), buf, dejaVu);
			buf.append(':');
			elementToString(e.getValue(), buf, dejaVu);
			has = true;
		}
		buf.append('}');
		dejaVu.remove(m);
	}

	private static void deepToString(
		Collection<Object> list,
		StringBuilder buf,
		Set<Object> dejaVu
	) {
		Object[] array = list.toArray();
		deepToString(array, buf, dejaVu);
	}

	private static void deepToString(Object[] a, StringBuilder buf, Set<Object> dejaVu) {
		if (a == null) {
			buf.append("null");
			return;
		}
		if (a.length == 0) {
			buf.append("[]");
			return;
		}
		dejaVu.add(a);
		buf.append('[');
		for (int i = 0; i < a.length; i++) {
			if (i != 0)
				buf.append(',');
			Object element = a[i];
			elementToString(element, buf, dejaVu);
		}
		buf.append(']');
		dejaVu.remove(a);
	}

	@SuppressWarnings("unchecked")
	private static void elementToString(Object element, StringBuilder buf, Set<Object> dejaVu) {
		if (element == null) {
			buf.append("null");
		} else {
			Class<?> eClass = element.getClass();
			if (eClass.isArray()) {
				if (eClass == byte[].class)
					buf.append(Arrays.toString((byte[]) element));
				else if (eClass == short[].class)
					buf.append(Arrays.toString((short[]) element));
				else if (eClass == int[].class)
					buf.append(Arrays.toString((int[]) element));
				else if (eClass == long[].class)
					buf.append(Arrays.toString((long[]) element));
				else if (eClass == char[].class)
					buf.append(Arrays.toString((char[]) element));
				else if (eClass == float[].class)
					buf.append(Arrays.toString((float[]) element));
				else if (eClass == double[].class)
					buf.append(Arrays.toString((double[]) element));
				else if (eClass == boolean[].class)
					buf.append(Arrays.toString((boolean[]) element));
				else { // element is an array of object references
					if (dejaVu.contains(element))
						buf.append("[...]");
					else
						deepToString((Object[]) element, buf, dejaVu);
				}
			} else { // element is non-null and not an array
				if (element instanceof Collection)
					deepToString((Collection<Object>) element, buf, dejaVu);
				else if (element instanceof Map)
					deepToString((Map<Object, Object>) element, buf, dejaVu);
				else if (element instanceof CharSequence)
					buf.append('"').append(element).append('"');
				else
					buf.append(element);
			}
		}
	}
}
