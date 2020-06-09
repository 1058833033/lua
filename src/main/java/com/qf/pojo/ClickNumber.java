package com.qf.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ChenJie
 * @date 2020-06-08 20:12:47
 * 功能说明
 */
@Data
@Accessors(chain = true)
public class ClickNumber implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer number;
}
