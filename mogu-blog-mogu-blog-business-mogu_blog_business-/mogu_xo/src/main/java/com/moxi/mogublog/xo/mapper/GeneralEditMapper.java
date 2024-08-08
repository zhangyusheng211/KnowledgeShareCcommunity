package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.GeneralEdit;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通用修改表 Mapper 接口
 *
 * @author 陌溪
 * @date 2022年3月7日22:15:22
 */
public interface GeneralEditMapper extends SuperMapper<GeneralEdit> {

    /**
     * 添加 修改表
     *
     * @param generalEdit
     * @return
     */
    Integer generalEdit(@Param("generalEdit") GeneralEdit generalEdit);
}
