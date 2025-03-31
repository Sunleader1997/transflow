package org.sunyaxing.transflow.common.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface HandleItem {
    String field();
    String label();
    boolean required() default true;
    String type() default "string";
    Class<?> javaType() default String.class;
    String defaultValue() default "";
}
