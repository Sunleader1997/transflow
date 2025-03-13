package org.sunyaxing.transflow.transflowapp.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;
import org.sunyaxing.transflow.transflowapp.entity.NodeLinkEntity;

@Repository
@CacheNamespace
public interface NodeLinkRepository extends BaseMapper<NodeLinkEntity> {
}
