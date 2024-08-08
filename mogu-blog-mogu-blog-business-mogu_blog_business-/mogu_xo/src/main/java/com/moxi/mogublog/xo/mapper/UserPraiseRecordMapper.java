package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserPraiseRecord;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 遇见
 */
@Mapper
public interface UserPraiseRecordMapper extends SuperMapper<UserPraiseRecord> {

    /**
     * 根据用户uids 查询 点赞数和
     *
     * @param users
     * @return
     */
    List<Map<String, String>> getUserPraiseRecordCountByUserId(@Param("users") List<User> users);
}
