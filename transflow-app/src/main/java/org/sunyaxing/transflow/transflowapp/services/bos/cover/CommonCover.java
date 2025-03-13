package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Named("CommonCover")
public class CommonCover {
    private static final Logger log = LoggerFactory.getLogger(CommonCover.class);

    @Named("generateIfNull")
    public Long generateIfNull(Long id) {
        return id == null ? IdUtil.getSnowflakeNextId() : id;
    }

    @Named("strToProperties")
    public Properties strToProperties(String properties) {
        try {
            return JSONObject.parseObject(properties, Properties.class);
        } catch (Exception e) {
            log.error("json字符串转换异常", e);
            return null;
        }
    }

    @Named("propertiesToStr")
    public String propertiesToStr(Properties properties) {
        return JSONObject.toJSONString(properties);
    }
}
