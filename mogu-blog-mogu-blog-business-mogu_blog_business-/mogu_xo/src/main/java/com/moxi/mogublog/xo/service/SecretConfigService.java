package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mogublog.commons.vo.SecretConfigVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 密钥配置表 服务类
 *
 * @author 陌溪
 * @date 2024年2月16日23:12:03
 */
public interface SecretConfigService extends SuperService<SecretConfig> {
    /**
     * 获取密钥配置列表
     *
     * @param secretConfigVO
     * @return
     */
    IPage<SecretConfig> getPageList(SecretConfigVO secretConfigVO);

    /**
     * 新增密钥配置
     *
     * @param secretConfigVO
     */
    String addSecretConfig(SecretConfigVO secretConfigVO);

    /**
     * 编辑密钥配置
     *
     * @param secretConfigVO
     */
    String editSecretConfig(SecretConfigVO secretConfigVO);

    /**
     * 批量删除密钥配置
     *
     * @param secretConfigVOList
     */
    String deleteBatchSecretConfig(List<SecretConfigVO> secretConfigVOList);


    SecretConfig getSecretConfig(String secretType, String subSecretType);
}
