package org.sunyaxing.transflow.transflowapp.services.bos.cover;

import cn.hutool.core.util.IdUtil;
import org.mapstruct.Named;

@Named("CommonCover")
public class CommonCover {
    @Named("generateIfNull")
    public Long generateIfNull(Long id) {
        return id == null ? IdUtil.getSnowflakeNextId() : id;
    }
}
