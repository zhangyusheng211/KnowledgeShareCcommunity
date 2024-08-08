package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.xo.service.GeneralEditService;
import com.moxi.mogublog.commons.vo.GeneralEditVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用修改表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月7日22:37:39
 */
@Api(value = "通用修改接口", tags = {"通用修改接口"})
@RestController
@RequestMapping("/generalEdit")
@Slf4j
public class GeneralEditRestApi {

    @Autowired
    private GeneralEditService generalEditService;

    @AvoidRepeatableCommit
    @BussinessLog(value = "新增勘误", behavior = EBehavior.EDIT_ANSWER)
    @ApiOperation(value = "新增勘误", notes = "新增勘误", response = String.class)
    @PostMapping("/generalEdit")
    public String generalEdit(@Validated({Insert.class}) @RequestBody GeneralEditVO generalEditVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("增加问题");
        return generalEditService.generalEdit(generalEditVO);
    }
}
