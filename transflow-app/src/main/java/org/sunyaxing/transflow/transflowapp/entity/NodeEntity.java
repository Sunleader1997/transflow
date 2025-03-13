package org.sunyaxing.transflow.transflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.sunyaxing.transflow.transflowapp.common.TransFlowTypeEnum;

import java.io.Serializable;

/**
 * transflow 各节点
 */

@Builder
@Data
@ToString
@TableName("node")
public class NodeEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("jobId")
    private Long jobId;

    @TableField("name")
    private String name;

    @TableField("nodeType")
    private TransFlowTypeEnum nodeType;

    @TableField("pluginId")
    private String pluginId;

    @TableField("config")
    private String config;

}
