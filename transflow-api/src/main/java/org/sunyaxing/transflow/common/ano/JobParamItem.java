package org.sunyaxing.transflow.common.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface JobParamItem {
    String field();
    String label();
    boolean required() default true;
    Class<?> type() default String.class;
}
