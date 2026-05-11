package com.example.aircraftwar.web;

import com.example.aircraftwar.scoredisplay.Score;

import java.util.List;

public interface getRankingCallBack {
    void onSuccess(List<Score> scores);  // 成功时调用，可携带服务端返回的消息
    void onFailure(String error);    // 失败时调用，返回错误原因
}
