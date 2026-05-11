package com.example.aircraftwar.web;

import com.example.aircraftwar.scoredisplay.Score;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class httpClient {
    // OkHttp客户端
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static String SERVER_IP = "10.0.2.2";
    private static String SERVER_PORT = "8080";

    public static void signUp(String username, String password, generalCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/signup?username=%s&password=%s",
                    SERVER_IP, SERVER_PORT, username, encryptPassword(password));
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        generalResponse resp = gson.fromJson(jsonStr, generalResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess();
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void Login(String username, String password, loginCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/login?username=%s&password=%s",
                    SERVER_IP, SERVER_PORT, username, encryptPassword(password));
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        LoginResponse resp = gson.fromJson(jsonStr, LoginResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess(resp.sessionID);
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void syncScore(String sessionID, int selfScore, syncScoreCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/gaming?sessionID=%s&score=%s",
                    SERVER_IP, SERVER_PORT, sessionID, selfScore);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        syncScoreResponse resp = gson.fromJson(jsonStr, syncScoreResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess(resp.oppScore);
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void requestMatching(String sessionID, String mode, requestMatchingCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/match?sessionID=%s&mode=%s",
                    SERVER_IP, SERVER_PORT, sessionID, mode);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        requestMatchingResponse resp = gson.fromJson(jsonStr, requestMatchingResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess(resp.oppName);
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void notifyGameOver(String sessionID, notifyGameOverCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/gameover?sessionID=%s",
                    SERVER_IP, SERVER_PORT, sessionID);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        notifyGameOverResponse resp = gson.fromJson(jsonStr, notifyGameOverResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess(resp.oppGameOver);
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void getUserRanking(String sessionID, getRankingCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/data?sessionID=%s&type=get",
                    SERVER_IP, SERVER_PORT, sessionID);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        getRankingResponse resp = gson.fromJson(jsonStr, getRankingResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess(resp.scores);
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void addUserRanking(String sessionID, int score, generalCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/data?sessionID=%s&type=add&score=%s",
                    SERVER_IP, SERVER_PORT, sessionID, score);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        generalResponse resp = gson.fromJson(jsonStr, generalResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess();
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void delUserRanking(String sessionID, Score score, generalCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/data?sessionID=%s&type=del&score=%s&recordTime=%s",
                    SERVER_IP, SERVER_PORT, sessionID, score.getScore(), score.getRecordTime());
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        generalResponse resp = gson.fromJson(jsonStr, generalResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess();
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    public static void delAllUserRanking(String sessionID, generalCallBack callBack) {
        try {
            // 拼接请求地址（带用户ID参数）
            String url = String.format("http://%s:%s/api/data?sessionID=%s&type=delAll",
                    SERVER_IP, SERVER_PORT, sessionID);
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get() // GET请求
                    .build();
            // 执行请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonStr = response.body().string();
                        Gson gson = new Gson();
                        generalResponse resp = gson.fromJson(jsonStr, generalResponse.class);
                        if(resp.state.equals("success")) {
                            callBack.onSuccess();
                        }
                        else {
                            callBack.onFailure(resp.message);
                        }
                    }
                    else {
                        callBack.onFailure("网络响应异常: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    callBack.onFailure("网络请求失败: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            // 捕获异常（网络错误、解析错误等）
            e.printStackTrace();
        }
    }

    private static String encryptPassword(String password) throws NoSuchAlgorithmException {
        // 获取MessageDigest的实例，指定为SHA-256算法
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 对字符串进行编码
        byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static class generalResponse {
        String state;
        String message;
    }

    private static class LoginResponse extends generalResponse {
        String sessionID;
    }

    private static class syncScoreResponse extends generalResponse {
        int oppScore;
    }

    private static class getRankingResponse extends generalResponse {
        List<Score> scores;
    }

    private static class requestMatchingResponse extends generalResponse {
        String oppName;
    }

    private static class notifyGameOverResponse extends generalResponse {
        boolean oppGameOver;
    }

    public static void setServerIp(String IP) {
        SERVER_IP = IP;
    }
    public static void setServerPort(String PORT) {
        SERVER_PORT = PORT;
    }
}
