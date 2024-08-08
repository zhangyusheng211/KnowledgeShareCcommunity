package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.GeneralEdit;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.vo.ProblemVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 问题表 Mapper 接口
 *
 * @author 陌溪
 * @date 2022年3月7日22:15:22
 */
public interface ProblemMapper extends SuperMapper<Problem> {

    /**
     * 查询问题表
     *
     * @param problemVO
     * @return
     */
    Page<Problem> queryPage(Page<ProblemVO> page, @Param("problemVO") ProblemVO problemVO);

    /**
     * 添加 修改表
     *
     * @param generalEdit
     * @return
     */
    Integer addEditAnswer(@Param("generalEdit") GeneralEdit generalEdit);
}
