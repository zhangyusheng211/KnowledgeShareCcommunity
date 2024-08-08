package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.UserEquityRecord;
import com.moxi.mogublog.commons.vo.UserEquityRecordVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户权益Mapper 接口
 *
 * @author 陌溪
 * @since 2021年12月18日23:20:00
 */
public interface UserEquityRecordMapper extends SuperMapper<UserEquityRecord> {
    /**
     * 查询用户权益列表
     *
     * @param userEquityRecordVO
     * @return
     */
    Page<UserEquityRecord> queryPage(Page page, @Param("blog") UserEquityRecordVO userEquityRecordVO);
}
