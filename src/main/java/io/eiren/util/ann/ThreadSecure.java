package io.eiren.util.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <p>
 * Методы, помеченные этой аннотацией должны быть полностью Thread-Safe.
 * </p>
 * <p>
 * <b>Важно:</b> данные методы гарантированно должны обеспечивать потоковую
 * безопасность и консистентность (полноту данных и точность синхронизации).
 * </p>
 * 
 * @see {@link ThreadSafe}, {@link Synchronize}, {@link ThreadSafeSingle}
 * @author Rena
 */
@Retention(value = RetentionPolicy.SOURCE)
public @interface ThreadSecure {

}
