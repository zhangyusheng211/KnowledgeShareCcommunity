package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.EmoticonService;
import com.moxi.mogublog.commons.vo.EmoticonVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 表情包表 RestApi
 *
 * @author 陌溪
 * @date 2022年10月29日18:19:57
 */
@Api(value = "表情包相关接口", tags = {"表情包相关接口"})
@RestController
@RequestMapping("/emoticon")
@Slf4j
public class EmoticonRestApi {

    @Resource
    EmoticonService emoticonService;

    @ApiOperation(value = "获取表情包列表", notes = "获取表情包列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody EmoticonVO emoticonVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        // 只能获取自己的表情包
        emoticonVO.setUserUid(RequestHolder.getUserUid());
        return ResultUtil.result(SysConf.SUCCESS, emoticonService.getPageList(emoticonVO));
    }

    @ApiOperation(value = "添加表情包", notes = "获取表情包列表", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody EmoticonVO emoticonVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return emoticonService.addEmoticon(emoticonVO);
    }

    @ApiOperation(value = "置顶表情包", notes = "置顶表情包", response = String.class)
    @PostMapping("/sticky")
    public String sticky(@Validated({Default.class}) @RequestBody EmoticonVO emoticonVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return emoticonService.stickyEmoticon(emoticonVO);
    }

    @ApiOperation(value = "批量删除表情包", notes = "批量删除表情包", response = String.class)
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestBody List<EmoticonVO> emoticonVOList) {
        return emoticonService.deleteBatchEmoticon(emoticonVOList);
    }
}

