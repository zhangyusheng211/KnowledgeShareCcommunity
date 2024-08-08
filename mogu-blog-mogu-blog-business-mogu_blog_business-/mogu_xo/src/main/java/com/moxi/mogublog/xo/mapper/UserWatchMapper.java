package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户关注表 Mapper 接口
 *
 * @author 陌溪
 * @since 2021年6月13日18:00:33
 */
public interface UserWatchMapper extends SuperMapper<UserWatch> {

    /**
     * 根据用户id 计算 关注数量
     *
     * @param users
     * @return
     */
    List<Map<String, String>> getUserWatchCountByUserId(@Param("users") List<User> users);
}
