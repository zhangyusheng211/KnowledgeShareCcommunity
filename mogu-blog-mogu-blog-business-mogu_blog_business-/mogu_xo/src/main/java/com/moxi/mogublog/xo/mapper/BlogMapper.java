package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mogublog.commons.vo.ContinuousDayVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 博客表 Mapper 接口
 *
 * @author 陌溪
 * @since 2018-09-08
 */
public interface BlogMapper extends SuperMapper<Blog> {

    /**
     * 通过标签获取博客数量
     *
     * @return
     */
    @Select("SELECT tag_uid, COUNT(tag_uid) as count FROM  t_blog where status = 1 GROUP BY tag_uid")
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 通过分类获取博客数量
     *
     * @return
     */
    @Select("SELECT blog_sort_uid, COUNT(blog_sort_uid) AS count FROM  t_blog where status = 1 GROUP BY blog_sort_uid")
    List<Map<String, Object>> getBlogCountByBlogSort();

    /**
     * 获取一年内的文章贡献数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, COUNT(uid) COUNT FROM t_blog WHERE 1=1 && status = 1 && create_time >= #{startTime} && create_time < #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getBlogContributeCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据传参条件 查询page
     *
     * @param page
     * @param blogVO
     * @return
     */
    Page<Blog> queryPage(Page page, @Param("blog") BlogVO blogVO);

    /**
     * 根据用户列表ID  查询各用户发表博客数
     *
     * @param users
     * @return
     */
    List<Map<String, String>> getUsersBolgCountByUserId(@Param("users") List<User> users);


    /**
     * 获取连续发表博客天数
     *
     * @param userUid
     * @param day
     * @return
     */
    ContinuousDayVO getPublishContinuousDays(@Param("userUid") String userUid, @Param("day") String day);
}
