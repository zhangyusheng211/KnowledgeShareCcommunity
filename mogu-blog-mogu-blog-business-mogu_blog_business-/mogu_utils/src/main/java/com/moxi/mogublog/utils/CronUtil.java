package com.moxi.mogublog.utils;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;

import java.text.ParseException;
import java.util.Date;

/**
 * Cron相关工具类
 */
public class CronUtil {
    /**
     * 判断当前日期是否符合cron表达式
     *
     * @param cronExpression cron表达式
     * @return true表示符合，false表示不符合
     */
    public static boolean isMatch(String cronExpression) {
        try {
            // 创建CronTrigger并解析cron表达式
            CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            String cronExpressionStr  = trigger.getCronExpression();
            CronExpression cronExpressionObj = new CronExpression(cronExpressionStr);
            // 获取当前日期并判断是否符合cron表达式
            Date now = new Date();
            return cronExpressionObj.isSatisfiedBy(now);
        } catch (ParseException e) {
            // 如果解析出错，说明不符合cron表达式
            return false;
        }
    }

    /**
     * 判断是否符合
     * @param cronExpression
     * @param dateTime
     * @return
     */
    public static boolean isMatch(String cronExpression, long dateTime) {
        try {
            // 创建CronTrigger并解析cron表达式
            CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            String cronExpressionStr  = trigger.getCronExpression();
            CronExpression cronExpressionObj = new CronExpression(cronExpressionStr);
            // 获取当前日期并判断是否符合cron表达式
            Date now = new Date(dateTime);
            return cronExpressionObj.isSatisfiedBy(now);
        } catch (ParseException e) {
            // 如果解析出错，说明不符合cron表达式
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(isMatch("0 0 0-23 1 3 ?"));
    }
}
