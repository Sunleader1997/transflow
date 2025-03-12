package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.TransFlowJobEntity;
import org.sunyaxing.transflow.transflowapp.repositories.TransFlowJobRepository;

@Service
public class TransFlowJobService extends ServiceImpl<TransFlowJobRepository, TransFlowJobEntity> {
}
