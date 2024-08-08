package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.commons.entity.MediaActor;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaActorService;
import com.moxi.mogublog.commons.vo.MediaActorVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 媒资演员Controller
 *
 * @author thh
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/media/actor")
public class MediaActorRestApi {
    @Autowired
    private MediaActorService mediaActorService;

    /**
     * 查询媒资演员列表
     */
    @ApiOperation(value = "获取媒资演员列表", notes = "获取媒资演员列表", response = String.class)
    @PostMapping("/list")
    public String list(@RequestBody MediaActorVO mediaActorVO) {
        return ResultUtil.successWithData(mediaActorService.getPageList(mediaActorVO));
    }


    /**
     * 查询没有被选择演员列表
     */
    @PostMapping("/notSelectedList")
    public String notSelectedList(@RequestBody MediaActorVO mediaActorVO) {
        return ResultUtil.successWithData(mediaActorService.selectNotSelectedList(mediaActorVO));
    }


    /**
     * 获取媒资演员详细信息
     */
    @ApiOperation(value = "获取媒资演员详细信息", notes = "获取媒资演员详细信息", response = String.class)
    @PostMapping(value = "/info")
    public String getInfo(@RequestBody MediaActorVO mediaActorVO) {
        return ResultUtil.successWithData(mediaActorService.getById(mediaActorVO.getUid()));
    }

    /**
     * 新增媒资演员
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增媒资演员详细信息", notes = "新增媒资演员详细信息", response = String.class)
    public String add(@RequestBody MediaActorVO mediaActorVO) {
        MediaActor mediaActor = new MediaActor();
        BeanUtils.copyProperties(mediaActorVO, mediaActor);
        mediaActor.setCollectCount(0);
        mediaActor.setClickCount(0);
        return ResultUtil.successWithData(mediaActorService.save(mediaActor));
    }

    /**
     * 修改媒资演员
     */
    @ApiOperation(value = "更新媒资演员详细信息", notes = "更新媒资演员详细信息", response = String.class)
    @PostMapping(value = "/edit")
    public String edit(@RequestBody MediaActorVO mediaActorVO) {
        MediaActor mediaActor = new MediaActor();
        BeanUtils.copyProperties(mediaActorVO, mediaActor);
        return ResultUtil.successWithData(mediaActorService.saveOrUpdate(mediaActor));
    }

    /**
     * 删除媒资演员
     */
    @ApiOperation(value = "删除媒资演员详细信息", notes = "删除媒资演员详细信息", response = String.class)
    @PostMapping("/deleteBatch")
    public String deteleBatch(@RequestBody List<String> uids) {
        return ResultUtil.successWithData(mediaActorService.removeByIds(uids));
    }
}
