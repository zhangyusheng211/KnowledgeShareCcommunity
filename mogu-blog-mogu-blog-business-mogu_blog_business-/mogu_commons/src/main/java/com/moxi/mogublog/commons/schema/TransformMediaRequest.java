package com.moxi.mogublog.commons.schema;

import lombok.Data;

@Data
public class TransformMediaRequest {

    /**
     * 媒资uid
     */
    String mediaUid;

    /**
     * 视频uid
     */
    String videoUid;
}
