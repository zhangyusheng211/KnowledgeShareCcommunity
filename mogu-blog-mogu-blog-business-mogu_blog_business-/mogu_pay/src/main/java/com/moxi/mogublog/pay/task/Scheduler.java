package com.moxi.mogublog.pay.task;

import com.moxi.mogublog.pay.manager.TradeManager;
import com.moxi.mogublog.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务调度器
 *
 * @author: 陌溪
 * @create: 2023年6月3日08:43:05
 */
@Slf4j
@Component
@EnableScheduling
public class Scheduler {

    @Resource
    RedisUtil redisUtil;

    @Resource
    TradeManager tradeManager;


    /**
     * 关闭未支付的订单【每小时更新一次】
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void closePayOrder() {
        log.info("##########开始执行定期关闭订单任务##########");
        tradeManager.closePayOrder(12);
        log.info("##########定期关闭订单任务执行结束##########");
    }

}
