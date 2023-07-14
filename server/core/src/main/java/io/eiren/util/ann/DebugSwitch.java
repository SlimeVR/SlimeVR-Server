package io.eiren.util.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DebugSwitch {
}
