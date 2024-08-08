package com.moxi.mogublog.sms.task.dto;

import lombok.Data;

@Data
public class Conf {
    String type;
    long target;
    String value;
}
