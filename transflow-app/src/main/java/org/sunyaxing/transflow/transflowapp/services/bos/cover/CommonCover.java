package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("CommonCover")
public class CommonCover {
    private static final Logger log = LoggerFactory.getLogger(CommonCover.class);

    @Named("generateIfNull")
    public Long generateIfNull(Long id) {
        return id == null ? IdUtil.getSnowflakeNextId() : id;
    }

    @Named("strToJSON")
    public JSONObject strToJSON(String jsonStr) {
        try {
            return JSONObject.parseObject(jsonStr);
        } catch (Exception e) {
            log.error("json字符串转换异常", e);
            return null;
        }
    }

    @Named("jsonToStr")
    public String jsonToStr(JSONObject jsonObject) {
        return jsonObject.toString();
    }
}
