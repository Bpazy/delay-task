package com.github.bpazy.delaytask;

import lombok.Data;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
@Data
public class DelayTask {
    private String taskId;
    private String targetClass;
    private String argument;

    private String failReason;
    private String taskStatus;
}
