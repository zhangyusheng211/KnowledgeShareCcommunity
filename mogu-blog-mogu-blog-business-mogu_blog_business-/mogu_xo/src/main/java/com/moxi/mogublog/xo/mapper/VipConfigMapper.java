package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员配置
 *
 * @author 陌溪
 * @date 2024年5月26日08:28:04
 */
@Mapper
public interface VipConfigMapper extends SuperMapper<VipConfig> {
}
