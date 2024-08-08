package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.SysDictData;
import com.moxi.mogublog.commons.vo.SysDictDataVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;
import java.util.Map;

/**
 * 字典数据 服务类
 *
 * @author 陌溪
 * @date 2020年2月15日21:06:45
 */
public interface SysDictDataService extends SuperService<SysDictData> {
    /**
     * 获取数据字典列表
     *
     * @param sysDictDataVO
     * @return
     */
    IPage<SysDictData> getPageList(SysDictDataVO sysDictDataVO);

    /**
     * 新增数据字典
     *
     * @param sysDictDataVO
     */
    String addSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 编辑数据字典
     *
     * @param sysDictDataVO
     */
    String editSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 批量删除数据字典
     *
     * @param sysDictDataVOList
     */
    String deleteBatchSysDictData(List<SysDictDataVO> sysDictDataVOList);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType
     * @return
     */
    Map<String, Object> getListByDictType(String dictType);

    /**
     * 根据字典类型数组获取字典数据
     *
     * @param dictTypeList
     * @return
     */
    Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);

    /**
     * 获取字典数据标签
     *
     * @param dictType
     * @return
     */
    Map<String, String> getDictDataLabelMapByType(String dictType);

}
