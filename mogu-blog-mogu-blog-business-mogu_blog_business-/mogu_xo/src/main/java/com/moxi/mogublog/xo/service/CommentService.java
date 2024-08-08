package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.vo.CommentVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 评论表 服务类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
public interface CommentService extends SuperService<Comment> {

    /**
     * 获取评论数目
     *
     * @author
     * @date 2018年10月22日下午3:43:38
     */
    Integer getCommentCount(CommentVO commentVO);

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @return
     */
    IPage<Comment> getPageList(CommentVO commentVO);


    /**
     * 根据业务id获取评论列表
     *
     * @param commentVO
     * @return
     */
    Map<String, List<Comment>> getCommentMapByBusinessId(CommentVO commentVO);

    /**
     * 新增评论
     *
     * @param commentVO
     */
    String addComment(CommentVO commentVO);

    /**
     * 编辑评论
     *
     * @param commentVO
     */
    String editComment(CommentVO commentVO);

    /**
     * 删除评论
     *
     * @param commentVO
     */
    String deleteComment(CommentVO commentVO);

    /**
     * 批量删除评论
     *
     * @param commentVOList
     */
    String deleteBatchComment(List<CommentVO> commentVOList);

    /**
     * @param blogUidList
     * @return
     */
    String batchDeleteCommentByBlogUid(List<String> blogUidList);

    /**
     * 评论转换
     *
     * @param commentList
     * @return
     */
    List<Comment> convertCommentList(Collection<Comment> commentList);

    /**
     * 审核评论
     *
     * @param commentVO
     * @return
     */
    String auditComment(CommentVO commentVO);

    /**
     * 获取用户评论列表
     *
     * @param commentVO
     * @return
     */
    IPage<Comment> getUserCommentList(CommentVO commentVO);
}
