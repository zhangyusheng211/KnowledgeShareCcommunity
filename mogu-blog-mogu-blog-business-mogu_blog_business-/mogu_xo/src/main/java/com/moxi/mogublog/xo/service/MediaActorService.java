package com.moxi.mogublog.xo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MediaActor;
import com.moxi.mogublog.commons.vo.MediaActorVO;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 演员Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaActorService extends SuperService<MediaActor> {

    IPage<MediaActor> getPageList(MediaActorVO mediaActorVO);

    IPage<MediaActor> selectNotSelectedList(MediaActorVO mediaActorVO);
}
