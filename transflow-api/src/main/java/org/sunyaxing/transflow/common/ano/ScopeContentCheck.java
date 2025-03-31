package org.sunyaxing.transflow.common.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ScopeContentCheck {
    JobParamItem[] value() default {};

    HandleItem handle() default @HandleItem(field = "", label = "");
}
