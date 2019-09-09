package com.yyzh.updateapplibrary.net;

public interface INetCallBack {
    void success(String response);

    void failed(Throwable throwable);
}
