package com.moxi.mogublog.web.restapi;


import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaActorService;
import com.moxi.mogublog.commons.vo.MediaActorVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list")
    @ApiOperation(value = "获取媒资演员列表", notes = "获取媒资演员列表", response = String.class)
    @PostMapping("/list")
    public String list(MediaActorVO mediaActorVO) {
        return ResultUtil.successWithData(mediaActorService.getPageList(mediaActorVO));
    }

    /**
     * 获取媒资演员详细信息
     */
    @ApiOperation(value = "获取媒资演员详细信息", notes = "获取媒资演员详细信息", response = String.class)
    @GetMapping(value = "/{uid}")
    public String getInfo(@PathVariable("uid") String uid) {
        return ResultUtil.successWithData(mediaActorService.getById(uid));
    }
}
