package com.example.aircraftwar.web;

public interface notifyGameOverCallBack {
    void onSuccess(boolean oppGameOver);  // 成功时调用，可携带服务端返回的消息
    void onFailure(String error);    // 失败时调用，返回错误原因
}
