package org.sunyaxing.transflow.transflowapp.config;

import lombok.Data;
import org.pf4j.Plugin;
import org.springframework.core.annotation.AnnotationUtils;
import org.sunyaxing.transflow.common.ano.HandleItem;
import org.sunyaxing.transflow.common.ano.JobParamItem;
import org.sunyaxing.transflow.common.ano.ScopeContentCheck;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobConfigProperties {
    private String key;
    private String label;
    private String type;
    private Boolean required;
    private Class<?> javaType;
    private String defaultValue;

    public static List<JobConfigProperties> getJobProperties(Plugin plugin) {
        ScopeContentCheck scopeContentCheck = AnnotationUtils.findAnnotation(plugin.getClass(), ScopeContentCheck.class);

        List<JobConfigProperties> jobProperties = new ArrayList<>();
        if (scopeContentCheck != null) {
            JobParamItem[] jobParamItems = scopeContentCheck.value();
            if (jobParamItems != null) {
                for (JobParamItem jobParamItem : jobParamItems) {
                    JobConfigProperties jobConfigProperties = new JobConfigProperties();
                    jobConfigProperties.setKey(jobParamItem.field());
                    jobConfigProperties.setLabel(jobParamItem.label());
                    jobConfigProperties.setType(jobParamItem.type());
                    jobConfigProperties.setJavaType(jobParamItem.javaType());
                    jobConfigProperties.setRequired(jobParamItem.required());
                    jobConfigProperties.setDefaultValue(jobParamItem.defaultValue());
                    jobProperties.add(jobConfigProperties);
                }
            }
        }
        return jobProperties;
    }
}
