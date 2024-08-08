package com.moxi.mogublog.picture.restapi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.picture.service.FileService;
import com.moxi.mougblog.base.vo.FileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feign")
@Api(value = "远程调用相关接口", tags = {"文件服务相关接口"})
@Slf4j
public class FeignRestApi {

    @Resource
    private FileService fileService;

    @FeignSecurity
    @ApiOperation(value = "截图上传", notes = "截图上传")
    @RequestMapping(value = "/getFileListMap", method = RequestMethod.POST)
    public Map<String, File> getFileListMap(@RequestBody FileVO fileVO) {
        IPage<File> fileIPage = fileService.getFileList(fileVO);
        List<File> fileList = fileIPage.getRecords();
        Map<String, File> fileMap = new HashMap<>();
        fileList.forEach(item -> {
            fileMap.put(item.getUid(), item);
        });
        return fileMap;
    }

}
