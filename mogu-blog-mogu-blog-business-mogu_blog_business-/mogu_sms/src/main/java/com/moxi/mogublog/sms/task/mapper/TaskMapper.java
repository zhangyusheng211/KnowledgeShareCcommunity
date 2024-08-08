package com.moxi.mogublog.sms.task.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxi.mogublog.sms.task.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TaskMapper extends BaseMapper<TaskConfig> {
}