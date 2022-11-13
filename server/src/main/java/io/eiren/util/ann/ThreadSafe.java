package io.eiren.util.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <p>
 * Методы, помеченные этой аннотацией должны быть Thread-Safe.
 * </p>
 * <p>
 * <b>Важно:</b> данные методы гарантированно должны обеспечивать потоковую
 * безопасность, но не обязаны обеспечивать концессивность (полноту данных или
 * точность синхронизации).
 * </p>
 * <p>
 * Для полностью потоко-безопасных методов можно использовать аннотацию
 * {@link ThreadSecure}.
 * </p>
 * 
 * @see {@link ThreadSecure}, {@link Synchronize}, {@link ThreadSafeSingle}
 * @author Rena
 */
@Retention(value = RetentionPolicy.SOURCE)
public @interface ThreadSafe {

}
