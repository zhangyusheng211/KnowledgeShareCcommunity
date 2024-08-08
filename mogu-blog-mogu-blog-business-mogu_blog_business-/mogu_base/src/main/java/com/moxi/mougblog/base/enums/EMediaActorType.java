package com.moxi.mougblog.base.enums;

/**
 * 电影演员关联类型
 *
 * @author thh
 */
public enum EMediaActorType {
    ACTOR("actor"), DIRECTOR("director");

    private final String value;

    EMediaActorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}