package io.eiren.util.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Marks methods and classes that use unsafe or direct access to memory. Proceed
 * with caution.
 * 
 * @author Rena
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface NativeUnsafe {

}
