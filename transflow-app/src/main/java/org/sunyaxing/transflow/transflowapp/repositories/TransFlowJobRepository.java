package org.sunyaxing.transflow.transflowapp.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.sunyaxing.transflow.transflowapp.entity.TransFlowJobEntity;

@Repository
public interface TransFlowJobRepository extends BaseMapper<TransFlowJobEntity> {
}
