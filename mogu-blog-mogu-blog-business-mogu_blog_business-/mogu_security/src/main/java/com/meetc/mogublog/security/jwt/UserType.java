package com.meetc.mogublog.security.jwt;

/**
 * @author 遇见
 * 用户端类型
 */
public enum UserType {
    DEFAULT("user"),
    USER("user"),
    ADMIN("admin");

    private String mark;

    UserType(String mark){
        this.mark = mark;
    }

    public static UserType getType(String mark){
        if (mark==null){
            return DEFAULT;
        }
        for (UserType userType:
             UserType.values()) {
            if (userType.mark.equals(mark)){
                return userType;
            }
        }
        return DEFAULT;
    }

    public String getMark(){
        return mark;
    }
}
