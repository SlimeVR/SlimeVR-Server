package io.eiren.util.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <p>
 * Означает необходимость обязательной синхронизации этого меcта во внешних
 * методах. В аргументах передаётся название поля для синхронизации.
 * </p>
 * <p>
 * Методы, помеченные данной аннотацией могут вызывать только Thread-Safe
 * методы, либо методы, помеченные такой же аннотацией с тем же полем
 * синхронизации.
 * </p>
 * <p>
 * Поля, помеченные данной аннотацией должны быть синхронизированны на указанное
 * поле при чтении или записи.
 * </p>
 * 
 * @see {@link ThreadSafe}, {@link ThreadSecure}, {@link ThreadSafeSingle}
 * @author Rena
 */
@Retention(value = RetentionPolicy.SOURCE)
public @interface Synchronize {

	String[] value();

}
