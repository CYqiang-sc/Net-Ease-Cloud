package com.sicnu.cloudmusic.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author 莲童的雨儿
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Result<T>
{
    private Integer code;

    private String msg;

    private T data;
}
