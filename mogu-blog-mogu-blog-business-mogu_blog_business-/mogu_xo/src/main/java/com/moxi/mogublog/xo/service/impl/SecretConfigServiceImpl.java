package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.SecretConfigMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽奖规则表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
@Slf4j
public class SecretConfigServiceImpl extends SuperServiceImpl<SecretConfigMapper, SecretConfig> implements SecretConfigService {

    @Resource
    SecretConfigService secretConfigService;
    @Resource
    RedisUtil redisUtil;

    @Override
    public IPage<SecretConfig> getPageList(SecretConfigVO secretConfigVO) {
        LambdaQueryWrapper<SecretConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(secretConfigVO.getKeyword()) && !StringUtils.isEmpty(secretConfigVO.getKeyword().trim())) {
            queryWrapper.like(SecretConfig::getBizSecret, secretConfigVO.getKeyword().trim()).or().like(SecretConfig::getBizId, secretConfigVO.getKeyword().trim());
        }
        if (secretConfigVO.getBizSecret() != null) {
            queryWrapper.eq(SecretConfig::getBizSecret, secretConfigVO.getBizSecret());
        }
        if (secretConfigVO.getSecretType() != null) {
            queryWrapper.eq(SecretConfig::getSecretType, secretConfigVO.getSecretType());
        }
        if (secretConfigVO.getSubSecretType() != null) {
            queryWrapper.eq(SecretConfig::getSubSecretType, secretConfigVO.getSubSecretType());
        }

        Page<SecretConfig> page = new Page<>();
        page.setCurrent(secretConfigVO.getCurrentPage());
        page.setSize(secretConfigVO.getPageSize());
        queryWrapper.eq(SecretConfig::getStatus, EStatus.ENABLE);
        IPage<SecretConfig> pageList = secretConfigService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addSecretConfig(SecretConfigVO secretConfigVO) {
        LambdaQueryWrapper<SecretConfig> queryWrapper = new LambdaQueryWrapper<SecretConfig>()
                .eq(SecretConfig::getSecretType, secretConfigVO.getSecretType())
                .eq(SecretConfig::getSubSecretType, secretConfigVO.getSubSecretType())
                .eq(SecretConfig::getStatus, EStatus.ENABLE).last(SysConf.LIMIT_ONE);
        SecretConfig containSecretConfig = secretConfigService.getOne(queryWrapper);
        if (containSecretConfig != null) {
            log.error("[getSecretConfig] 该密钥配置已存在，请勿重复配置; secretType: {}, subSecretType: {}",secretConfigVO.getSecretType(), secretConfigVO.getSubSecretType());
            throw new QueryException("该密钥配置已存在，请勿重复配置");
        }

        SecretConfig secretConfig = new SecretConfig();
        BeanUtils.copyProperties(secretConfigVO, secretConfig, SysConf.STATUS);
        boolean opSuccess = secretConfig.insert();
        if (opSuccess) {
            redisUtil.delete(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + secretConfig.getSecretType() + SysConf.REDIS_SEGMENTATION + secretConfig.getSubSecretType());
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editSecretConfig(SecretConfigVO secretConfigVO) {
        SecretConfig secretConfig = secretConfigService.getById(secretConfigVO.getUid());
        if (secretConfig == null) {
            throw new QueryException("参数异常");
        }
        LambdaQueryWrapper<SecretConfig> queryWrapper = new LambdaQueryWrapper<SecretConfig>()
                .eq(SecretConfig::getSecretType, secretConfigVO.getSecretType())
                .eq(SecretConfig::getSubSecretType, secretConfigVO.getSubSecretType())
                .eq(SecretConfig::getStatus, EStatus.ENABLE).last(SysConf.LIMIT_ONE);
        SecretConfig containSecretConfig = secretConfigService.getOne(queryWrapper);
        if (containSecretConfig != null && !containSecretConfig.getUid().equals(secretConfig.getUid())) {
            log.error("[getSecretConfig] 该密钥配置已存在，请勿重复配置; secretType: {}, subSecretType: {}",secretConfigVO.getSecretType(), secretConfigVO.getSubSecretType());
            throw new QueryException("该密钥配置已存在，请勿重复配置");
        }
        BeanUtils.copyProperties(secretConfigVO, secretConfig, SysConf.STATUS, SysConf.UID);
        boolean opSuccess = secretConfig.updateById();
        // 更新后清空Redis配置
        if (opSuccess) {
            redisUtil.delete(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + secretConfigVO.getSecretType() + SysConf.REDIS_SEGMENTATION + secretConfigVO.getSubSecretType());
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSecretConfig(List<SecretConfigVO> secretConfigVOList) {
        if (secretConfigVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        secretConfigVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        List<SecretConfig> secretConfigList = secretConfigService.listByIds(uids);
        secretConfigList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            redisUtil.delete(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + item.getSecretType() + SysConf.REDIS_SEGMENTATION + item.getSubSecretType());
        });
        boolean save = secretConfigService.updateBatchById(secretConfigList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public SecretConfig getSecretConfig(String secretType, String subSecretType) {

        //从Redis中获取内容
        String jsonResult = redisUtil.get(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + secretType + SysConf.REDIS_SEGMENTATION + subSecretType);
        //判断redis中是否有字典
        if (StringUtils.isNotEmpty(jsonResult)) {
            SecretConfig secretConfig = JsonUtils.jsonToPojo(jsonResult, SecretConfig.class);
            if (secretConfig != null) {
                return secretConfig;
            }
        }

        LambdaQueryWrapper<SecretConfig> queryWrapper = new LambdaQueryWrapper<SecretConfig>()
                .eq(SecretConfig::getSecretType, secretType)
                .eq(SecretConfig::getSubSecretType, subSecretType)
                .eq(SecretConfig::getIsPublish, EPublish.PUBLISH)
                .eq(SecretConfig::getStatus, EStatus.ENABLE).last(SysConf.LIMIT_ONE);
        SecretConfig secretConfig = secretConfigService.getOne(queryWrapper);
        if (secretConfig == null) {
            log.error("[getSecretConfig] 系统未查询到该密钥配置，请到后台密钥管理处添加; secretType: {}, subSecretType: {}",secretType, subSecretType);
            throw new QueryException("系统未查询到该密钥配置，请到后台密钥管理处添加");
        }
        // 将配置写入缓存中
        redisUtil.setEx(SysConf.REDIS_DICT_TYPE + SysConf.REDIS_SEGMENTATION + secretType + SysConf.REDIS_SEGMENTATION + subSecretType, JsonUtils.objectToJson(secretConfig), 1, TimeUnit.DAYS);
        return secretConfig;
    }
}
