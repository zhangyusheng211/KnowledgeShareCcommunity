package com.moxi.mogublog.xo.mapper;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserMoment;
import com.moxi.mogublog.commons.vo.UserMomentVO;
import com.moxi.mogublog.xo.dto.UserMomentDto;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户动态 Mapper 接口
 *
 * @author 陌溪
 * @date 2021年12月25日23:52:49
 */
public interface UserMomentMapper extends SuperMapper<UserMoment> {
    /**
     * 根据查询信息
     * 查询动态
     *
     * @param page
     * @param userMomentVO
     * @param toUserUidList
     * @return
     */
    Page<UserMomentDto> list(Page<UserMomentDto> page, @Param("userMomentVO") UserMomentVO userMomentVO, @Param("users") List<String> toUserUidList);

    /**
     * 根据用户id查询动态数
     *
     * @param users
     * @return
     */
    List<Map<String, String>> getUserMomentCountByUserId(@Param("users") List<User> users);
}
