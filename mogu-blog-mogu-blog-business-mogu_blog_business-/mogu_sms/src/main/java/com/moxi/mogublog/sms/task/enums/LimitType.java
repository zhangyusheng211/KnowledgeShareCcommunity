package com.moxi.mogublog.sms.task.enums;

import cn.hutool.core.date.DateUtil;
import com.moxi.mogublog.sms.task.dto.UserTask;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public enum LimitType {
    DAY {
        @Override
        public boolean exipre(UserTask task, long lastUpdate) {
            return !DateUtil.isSameDay(new Date(), new Date(lastUpdate));
        }
    },
    WEEK {
        @Override
        public boolean exipre(UserTask task, long lastUpdate) {
            return !DateUtil.isSameWeek(new Date(), new Date(lastUpdate), true);
        }
    },
    MONTH {
        @Override
        public boolean exipre(UserTask task, long lastUpdate) {
            return !DateUtil.isSameMonth(new Date(), new Date(lastUpdate));
        }
    },
    EVER {
        @Override
        public boolean exipre(UserTask task, long lastUpdate) {
            return false;
        }
    },

    // cron表达式重复过期, 无效表达式认为非过期
    CRON {
        @Override
        public boolean exipre(UserTask task, long lastUpdate) {
//            if (task == null) {
//                return false;
//            }
//            if (CronExpression.isValidExpression(task.getLimitCron())) {
//                 // 获取该表达式下一个日期
//                 LocalDateTime localDateTime = CronExpression.parse(task.getLimitCron())
//                        .next(LocalDateTime.ofEpochSecond(lastUpdate / 1000, 0, ZoneOffset.ofHours(8)));
//                 // 判断下一个日期是否在同一天
//                ZoneId zoneId = ZoneId.systemDefault(); // 获取系统默认时区
//                ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId); // 将 LocalDateTime 转换为带有时区信息的对象
//                Date date = Date.from(zonedDateTime.toInstant());
//                boolean isSameDay = DateUtil.isSameDay(new Date(), date);
//                return !isSameDay;
//
////                return CronExpression.parse(task.getLimitCron())
////                        .next(LocalDateTime.ofEpochSecond(lastUpdate / 1000, 0, ZoneOffset.ofHours(8)))
////                        .isBefore(LocalDateTime.now());
//            }
            return false;
        }
    };

    /**
     * 判断是否过期失效
     *
     * @param task
     * @param lastUpdate 毫秒
     */
    public abstract boolean exipre(UserTask task, long lastUpdate);
}