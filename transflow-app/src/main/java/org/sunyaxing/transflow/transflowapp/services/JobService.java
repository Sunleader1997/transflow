package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.repositories.JobRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

@Service
public class JobService extends ServiceImpl<JobRepository, JobEntity> {
    public void save(JobBo jobBo) {
        JobEntity jobEntity = BoCover.INSTANCE.boToEntity(jobBo);
        if (jobBo.getId() == null) {
            this.save(jobEntity);
        } else {
            this.updateById(jobEntity);
        }
    }
}
