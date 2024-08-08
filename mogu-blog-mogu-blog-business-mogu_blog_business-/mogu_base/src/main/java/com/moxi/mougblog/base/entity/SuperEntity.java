package com.moxi.mougblog.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.enums.EStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * Entity基类
 *
 * @author 陌溪
 * @date 2018年9月17日09:47:12
 */
// 在 Java 中，@Data 是 Lombok 库提供的一个注解，用于自动生成常见的 JavaBean 方法，
// 如 getter、setter、toString、equals、hashCode 和构造方法。使用 @Data 可以简化代码，提高开发效率。
// 在 Java 中，@SuppressWarnings 注解用于抑制编译器的特定警告。"rawtypes" 是其中一种常见的警告类型，它用于抑制与原始类型（raw types）相关的警告.
@Data
@SuppressWarnings("rawtypes")
public class SuperEntity<T extends Model> extends Model {

    /**
     *
     */
    // z serialVersionUID 用于版本控制，确保序列化和反序列化时类版本一致。
    private static final long serialVersionUID = -4851055162892178225L;

    /**
     * 唯一UID
     */
    // Z @TableId 注解, 用于标注实体类的主键字段   value：主键字段对应的数据库列名。
    //                                         type：主键生成策略，对应 IdType 枚举。
    @TableId(value = "uid", type = IdType.ASSIGN_UUID)
    private String uid;

    /**
     * 状态 0：失效  1：生效
     */
    private int status;

    /**
     * @TableField 配置需要填充的字段
     * 创建时间
     */
    // z 在执行数据库插入操作时，自动填充创建时间字段。
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     * z 插入和更新时都会自动填充
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public SuperEntity() {
        // 构造方法初始化，状态为1
        this.status = EStatus.ENABLE;
    }
}