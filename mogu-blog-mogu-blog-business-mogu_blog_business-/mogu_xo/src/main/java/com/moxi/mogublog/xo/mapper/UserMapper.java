package com.moxi.mogublog.xo.mapper;


import com.moxi.mogublog.commons.entity.User;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 Mapper 接口
 *
 * @author 陌溪
 * @since 2018-09-04
 */
public interface UserMapper extends SuperMapper<User> {
    /**
     * 修改用户积分 默认为加   如果是减   传递负值
     *
     * @param userUid
     * @param credits
     */
    void updateCreditsByUserUid(@Param("userUid") String userUid, @Param("credits") Integer credits);

    /**
     * 更新用户经验值
     * @param userUid
     * @param expValue
     */
    void updateExpValueByUserUid(@Param("userUid") String userUid, @Param("expValue") Integer expValue);

    /**
     * 获取用户排名
     *
     * @param userUid
     * @return
     */
    Integer getUserRank(@Param("userUid") String userUid);

    /**
     * 更新用户余额
     * @param userUid
     * @param operateAmount
     */
    void updateAmountByUserUid(@Param("userUid") String userUid, @Param("operateAmount") Long operateAmount);
}
