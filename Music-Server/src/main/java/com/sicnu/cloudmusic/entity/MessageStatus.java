package com.sicnu.cloudmusic.entity;

import lombok.Data;

/**
 * @author 莲童的雨儿
 */
@Data
public class MessageStatus
{
    private Message message;

    /**
     * 0未读 1已读
     */
    private Integer status;
}
