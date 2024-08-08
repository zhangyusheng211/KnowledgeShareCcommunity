package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 评论表 Mapper 接口
 *
 * @author 陌溪
 * @since 2018-09-08
 */
public interface CommentMapper extends SuperMapper<Comment> {

    /**
     * 根据user_ids 查询用户评论数
     *
     * @param users
     * @return
     */
    List<Map<String, String>> getCommentCountByUserId(@Param("users") List<User> users);
}
