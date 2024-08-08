package com.moxi.mogublog.xo.mapper;


import com.moxi.mogublog.commons.entity.MediaInfoActor;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 媒资演员关联Mapper接口
 *
 * @author thh
 * @date 2021-12-14
 */
@Repository
public interface MediaInfoActorMapper extends SuperMapper<MediaInfoActor> {

    /**
     * 查询演员列表
     *
     * @param mediaInfoActor
     * @return
     */
    List<MediaInfoActor> selectMediaInfoActorList(MediaInfoActor mediaInfoActor);
}
