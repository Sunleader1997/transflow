package org.sunyaxing.transflow.transflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
 
/**
 * TransFlow 工作流 实体
 */
@Builder
@Data
@ToString
@TableName("job")
public class JobEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
 
    @TableField("name")
    private String name;

    @TableField("description")
    private String description;
}