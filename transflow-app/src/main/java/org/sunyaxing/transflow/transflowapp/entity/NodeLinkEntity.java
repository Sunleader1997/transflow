package org.sunyaxing.transflow.transflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 节点连接线
 */
@Builder
@Data
@ToString
@TableName("node_link")
public class NodeLinkEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @TableField("sourceId")
    private String sourceId;

    @TableField("sourceHandle")
    private String sourceHandle;

    @TableField("targetId")
    private String targetId;

    @TableField("targetHandle")
    private String targetHandle;
}
