package com.moxi.mogublog.sms.task.enums;

import lombok.Getter;

@Getter
public enum CloudRecordType {
    invite("邀请好友"),
    sign("签到"),
    signs("连签礼"),
    time("限时福利"),
    task("完成任务"),
    task1("完成任务"),
    task2("完成任务"),
    task3("完成任务"),
    task4("完成任务"),
    exchange("兑换"),
    draw("抽奖"),
    give("赠送"),
    ;

    private String text;

    CloudRecordType(String string) {
        this.text = string;
    }
}