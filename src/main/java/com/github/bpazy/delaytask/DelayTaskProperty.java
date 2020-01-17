package com.github.bpazy.delaytask;

import lombok.Builder;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
@Builder
public class DelayTaskProperty {
    private String applicationName;

    public String getKey() {
        return "delaytask:" + applicationName;
    }
}
