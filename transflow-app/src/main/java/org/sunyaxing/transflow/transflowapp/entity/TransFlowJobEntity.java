package org.sunyaxing.transflow.transflowapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
 
/**
 * @author hxstrive.com
 * @since 1.0.0  2024/9/24 13:56
 */
@Builder
@Data
@ToString
@TableName("job")
public class TransFlowJobEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
 
    @TableField("name")
    private String name;
}