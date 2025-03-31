package org.sunyaxing.transflow.transflowapp.config;

import lombok.Data;
import org.pf4j.Plugin;
import org.springframework.core.annotation.AnnotationUtils;
import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;

@Data
public class HandlerProperties {
    private String key;
    private String label;
    private String type;
    private Boolean required;
    private Class<?> javaType;
    private String defaultValue;

    public static HandlerProperties getHandler(Plugin plugin) {
        ScopeContentCheck scopeContentCheck = AnnotationUtils.findAnnotation(plugin.getClass(), ScopeContentCheck.class);
        if (scopeContentCheck != null) {
            HandleItem handleItem = scopeContentCheck.handle();
            HandlerProperties handlerProperties = new HandlerProperties();
            handlerProperties.setKey(handleItem.field());
            handlerProperties.setLabel(handleItem.label());
            handlerProperties.setType(handleItem.type());
            handlerProperties.setJavaType(handleItem.javaType());
            handlerProperties.setRequired(handleItem.required());
            handlerProperties.setDefaultValue(handleItem.defaultValue());
            return handlerProperties;
        }
        return null;
    }
}
