package com.moxi.mogublog.sms.task.enums;

/**
 * 任务标识
 */
public enum TaskSign {

    /**
     * 任务
     */
    TASK("task", "任务"),

    /**
     * 成就
     */
    ACHIEVE("achieve", "成就"),
    ;
    private final String type;
    private final String name;

    TaskSign(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}