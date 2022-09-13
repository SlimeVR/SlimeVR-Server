package io.eiren.util.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Соблюдает те же требования что и {@link ThreadSafe} но при условии, что сам
 * метод вызывается только из одного потока одновременно.
 * 
 * @see {@link ThreadSafe}, {@link ThreadSecure}, {@link Synchronize}
 * @author Rena
 */
@Retention(value = RetentionPolicy.SOURCE)
public @interface ThreadSafeSingle {

}
