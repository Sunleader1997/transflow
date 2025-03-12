package org.sunyaxing.transflow.transflowapp.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.sunyaxing.transflow.transflowapp.entity.NodeEntity;

@Repository
public interface NodeRepository extends BaseMapper<NodeEntity> {
}
