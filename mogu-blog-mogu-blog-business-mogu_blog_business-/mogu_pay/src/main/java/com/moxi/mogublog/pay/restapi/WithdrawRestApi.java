package com.moxi.mogublog.pay.restapi;

import com.moxi.mogublog.commons.vo.WithdrawVO;
import com.moxi.mogublog.pay.service.WithdrawService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.holder.RequestHolder;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 提现服务API接口
 */
@RestController
@RequestMapping("/withdraw")
@Api(value = "提现模块", tags = {"提现模块"})
public class WithdrawRestApi {

    @Resource
    private WithdrawService withdrawService;

    /**
     * 审核提现
     *
     * @param withdrawVO
     * @return
     */
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public String auditWithdraw(@RequestBody WithdrawVO withdrawVO) {
        return withdrawService.auditWithdraw(withdrawVO);
    }

    /**
     * 发起提现
     *
     * @param withdrawVO
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addWithdraw(@RequestBody WithdrawVO withdrawVO) {
        return withdrawService.addWithdraw(withdrawVO);
    }

    /**
     * 获取提现列表
     *
     * @param withdrawVO
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public String withdrawList(@RequestBody WithdrawVO withdrawVO) {
        return ResultUtil.successWithData(withdrawService.getPageList(withdrawVO));
    }

    /**
     * 获取当前用户最近的一次提现
     * @return
     */
    @RequestMapping(value = "/getRecentWithdraw", method = RequestMethod.POST)
    public String getRecentWithdraw() {
        return ResultUtil.successWithData(withdrawService.getRecentWithdraw());
    }


    /**
     * 获取我的提现金额
     *
     * @param withdrawVO
     * @return
     */
    @RequestMapping(value = "/getMyWithdrawAmount", method = RequestMethod.POST)
    public String getMyWithdrawAmount(@RequestBody WithdrawVO withdrawVO) {
        withdrawVO.setUserUid(RequestHolder.getUserUid());
        return ResultUtil.successWithData(withdrawService.getWithdrawAmount(withdrawVO));
    }
}
