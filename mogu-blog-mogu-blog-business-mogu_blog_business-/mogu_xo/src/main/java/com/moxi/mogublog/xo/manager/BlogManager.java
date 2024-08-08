package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.vo.BlogVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 遇见
 */
public interface BlogManager {
    /**
     * 新增博客
     *
     * @param blogVO
     * @return
     */
    String addBlog(BlogVO blogVO);

    /**
     * 上传本地博客
     *
     * @param filedatas
     * @return
     */
    String uploadLocalBlog(List<MultipartFile> filedatas);

    /**
     * 修改博客
     *
     * @param blogVO
     * @return
     */
    String editBlog(BlogVO blogVO);

    /**
     * 发布/下架博客
     *
     * @param blogVO
     * @return
     */
    String publish(BlogVO blogVO);

    /**
     * 删除博客
     *
     * @param blogVO
     * @return
     */
    String deleteBlog(BlogVO blogVO);
}
