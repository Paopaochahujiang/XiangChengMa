package com.baiyun.xiangchengma.Listener.impl;

import com.baiyun.xiangchengma.Listener.OnRequireRefreshListener;

public class RequireHandle {

    public static OnRequireRefreshListener refreshListener;

    public RequireHandle(OnRequireRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public static OnRequireRefreshListener getOnlidelistener() {

        return refreshListener;
    }

    public void cancel() {
        refreshListener = null;
    }
}