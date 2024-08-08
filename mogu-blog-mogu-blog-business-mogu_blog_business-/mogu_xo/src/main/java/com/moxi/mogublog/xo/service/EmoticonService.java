package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Emoticon;
import com.moxi.mogublog.commons.vo.EmoticonVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 表情包表 服务类
 *
 * @author 陌溪
 * @date 2022年10月29日17:55:232
 */
public interface EmoticonService extends SuperService<Emoticon> {

    /**
     * 获取表情包列表
     *
     * @param emoticonVO
     * @return
     */
    IPage<Emoticon> getPageList(EmoticonVO emoticonVO);

    /**
     * 新增表情包
     *
     * @param emoticonVO
     * @return
     */
    String addEmoticon(EmoticonVO emoticonVO);

    /**
     * 编辑表情包
     *
     * @param emoticonVO
     * @return
     */
    String editEmoticon(EmoticonVO emoticonVO);

    /**
     * 置顶表情包
     *
     * @param emoticonVO
     * @return
     */
    String stickyEmoticon(EmoticonVO emoticonVO);

    /**
     * 批量删除表情包
     *
     * @param emoticonVOList
     * @return
     */
    String deleteBatchEmoticon(List<EmoticonVO> emoticonVOList);
}
