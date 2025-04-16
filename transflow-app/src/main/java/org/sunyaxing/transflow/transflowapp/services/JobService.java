package org.sunyaxing.transflow.transflowapp.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sunyaxing.transflow.transflowapp.entity.JobEntity;
import org.sunyaxing.transflow.transflowapp.repositories.JobRepository;
import org.sunyaxing.transflow.transflowapp.services.bos.JobBo;
import org.sunyaxing.transflow.transflowapp.services.bos.cover.BoCover;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JobService extends ServiceImpl<JobRepository, JobEntity> {

    public JobBo boById(String jobId) {
        JobEntity jobEntity = this.getById(jobId);
        return BoCover.INSTANCE.entityToBo(jobEntity);
    }

    public void save(JobBo jobBo) {
        JobEntity jobEntity = BoCover.INSTANCE.boToEntity(jobBo);
        jobEntity.setUpdateTime(new Date());
        if (jobBo.getId() == null) {
            this.save(jobEntity);
        } else {
            this.updateById(jobEntity);
        }
    }

    public List<JobBo> listAll() {
        return lambdaQuery()
                .orderByDesc(JobEntity::getUpdateTime)
                .list()
                .stream().map(BoCover.INSTANCE::entityToBo)
                .collect(Collectors.toList());
    }
    public JobBo getBoById(String id) {
        JobEntity jobEntity = this.getById(id);
        if(Objects.isNull(jobEntity)) return null;
        return BoCover.INSTANCE.entityToBo(jobEntity);
    }
}
