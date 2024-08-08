package com.moxi.mogublog.web.restapi;


import com.moxi.mogublog.commons.vo.MediaPlayRecordVO;
import com.moxi.mogublog.commons.vo.MediaTagVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaPlayRecordService;
import com.moxi.mogublog.xo.service.MediaTagService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 媒资用户播放记录
 *
 * @author 陌溪
 * @date 2024年1月7日19:14:11
 */
@RestController
@RequestMapping("/media/playMediaRecord")
public class MediaPlayLogRestApi {
    @Resource
    private MediaPlayRecordService mediaPlayRecordService;

    /**
     * 查询播放记录
     * @param mediaPlayRecordVO
     * @return
     */
    @GetMapping("/list")
    public String list(@RequestBody MediaPlayRecordVO mediaPlayRecordVO) {

        return ResultUtil.successWithData(mediaPlayRecordService.getPageList(mediaPlayRecordVO));
    }


    /**
     * 获取播放记录详细信息
     */
    @PostMapping("/getMediaPlayRecord")
    public String getMediaPlayRecord(@RequestBody MediaPlayRecordVO mediaPlayRecordVO) {
        return ResultUtil.successWithData(mediaPlayRecordService.selectPlayRecordsByVideoId(mediaPlayRecordVO.getVideoUid()));
    }

    /**
     * 新增或者更新播放记录
     */
    @PostMapping("/insertOrUpdate")
    public String insertOrUpdate(@RequestBody MediaPlayRecordVO mediaPlayRecordVO) {
        return mediaPlayRecordService.insertOrUpdateMediaPlayRecord(mediaPlayRecordVO);
    }
}
