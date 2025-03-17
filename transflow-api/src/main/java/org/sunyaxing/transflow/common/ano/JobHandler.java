package org.sunyaxing.transflow.common.ano;

import org.sunyaxing.transflow.common.HandlerEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface JobHandler {
    HandlerEnums type() default HandlerEnums.SOURCE;
}
