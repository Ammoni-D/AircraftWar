package com.example.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class httpServer {
    private static final String DB_URL = "jdbc:sqlite:httpServer/data/userinfo.db";
    private static final Map<String, String> connect = new HashMap<>();
    private static final List<Room> rooms = new ArrayList<>();
    private static final Map<String, Room> user_room = new HashMap<>();
    private static void initServer() {
        Path dataPath = Paths.get("httpServer/data");
        if(!Files.exists(dataPath)) {
            try {
                Files.createDirectory(dataPath);
            } catch (IOException e) {
                System.out.println("服务器初始化失败：文件夹data创建失败" + e.getMessage());
            }
        }
        // 创建数据库并建表
        createAuthTable();
        createScoreTable();
    }

    public static void main(String[] args) throws IOException {
        // 1. 创建服务器，绑定8080端口，最大并发数10
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);
        // 2. 初始化服务器数据
        httpServer.initServer();
        // 3. 注册各类用户接口：/api/signup等
        server.createContext("/api/signup", new signUpHandler());
        server.createContext("/api/login", new loginHandler());
        server.createContext("/api/match", new matchHandler());
        server.createContext("/api/gaming", new gamingHandler());
        server.createContext("/api/gameover", new gameOverHandler());
        server.createContext("/api/data", new dataHandler());
        // 4. 启动服务器
        server.start();
        System.out.println("用户信息服务器启动成功！");
    }

    // 处理用户注册
    static class signUpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            if (query != null && query.contains("username=") && query.contains("password=")) {
                String username = query.split("&")[0].split("=")[1];
                String password = query.split("&")[1].split("=")[1];
                try {
                    register(username, password);
                    state = "success";
                }
                catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData = "{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"message\": \"" + errorMessage + "\"\n" +
                    "}";
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 处理用户登录
    static class loginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            String sessionID = "";
            if (query != null && query.contains("username=") && query.contains("password=")) {
                String username = query.split("&")[0].split("=")[1];
                String password = query.split("&")[1].split("=")[1];
                try {
                    if(login(username, password)) {
                        sessionID = UUID.randomUUID().toString();
                        connect.put(sessionID, username);
                        state = "success";
                    }
                    else errorMessage = "用户名或密码错误";
                }
                catch (Exception e) {
                    errorMessage = e.getMessage();
                }
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData = "{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"message\": \"" + errorMessage + "\",\n" +
                    "  \"sessionID\": \"" + sessionID + "\"\n" +
                    "}";
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 处理匹配对战
    static class matchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            String oppName = "";
            if (query != null && query.contains("sessionID=") && query.contains("mode=")) {
                String sessionID = query.split("&")[0].split("=")[1];
                String username = connect.get(sessionID);
                String mode = query.split("&")[1].split("=")[1];
                if(!user_room.containsKey(username)) {
                    user_room.put(username, allocateRoom(username, mode));
                }
                Room room = user_room.get(username);
                if(room.isFull()) {
                    state = "success";
                    oppName = room.getOppName(username);
                }
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData = "{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"message\": \"" + errorMessage + "\",\n" +
                    "  \"oppName\": \"" + oppName + "\"\n" +
                    "}";
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 处理分数同步
    static class gamingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            int oppScore = 0;
            if (query != null && query.contains("sessionID=") && query.contains("score=")) {
                String sessionID = query.split("&")[0].split("=")[1];
                String username = connect.get(sessionID);
                int score = Integer.parseInt(query.split("&")[1].split("=")[1]);
                Room room = user_room.get(username);
                room.setScore(username, score);
                oppScore = room.getOppScore(username);
                state = "success";
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData = "{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"message\": \"" + errorMessage + "\",\n" +
                    "  \"oppScore\": \"" + oppScore + "\"\n" +
                    "}";
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 处理一方游戏结束
    static class gameOverHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            boolean oppGameOver = false;
            if (query != null && query.contains("sessionID=")) {
                String sessionID = query.split("=")[1];
                String username = connect.get(sessionID);
                Room room = user_room.get(username);
                // 释放房间
                if(!room.isAlive1() && !room.isAlive2()) {
                    user_room.remove(username);
                    user_room.remove(room.getOppName(username));
                    room.resetRoom();
                    oppGameOver = true;
                }
                else {
                    room.gameOver(username);
                    if (!room.oppAlive(username)) {
                        oppGameOver = true;
                    }
                }
                state = "success";
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData = "{\n" +
                    "  \"state\": \"" + state + "\",\n" +
                    "  \"message\": \"" + errorMessage + "\",\n" +
                    "  \"oppGameOver\": " + oppGameOver + "\n" +
                    "}";
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 处理所有数据请求
    static class dataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应头（允许跨域、指定JSON格式）
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            // 根据请求参数返回对应信息
            String query = exchange.getRequestURI().getQuery();
            String errorMessage = "default";
            String state = "fail";
            String requestType = null;
            boolean oppGameOver = false;
            String queryResult = null;
            if (query != null && query.contains("sessionID=") && query.contains("type=")) {
                String sessionID = query.split("&")[0].split("=")[1];
                String username = connect.get(sessionID);
                requestType = query.split("&")[1].split("=")[1];
                boolean formatError = false;
                switch (requestType) {
                    case "get" : {
                        queryResult = queryAllScore(username);
                        break;
                    }
                    case "add" : {
                        if(query.contains("score=")) {
                            int score = Integer.parseInt(query.split("&")[2].split("=")[1]);
                            insertScore(username, score);
                        }
                        else formatError = true;
                        break;
                    }
                    case "del" : {
                        if(query.contains("score=") && query.contains("recordTime=")) {
                            int score = Integer.parseInt(query.split("&")[2].split("=")[1]);
                            String recordTime = query.split("&")[3].split("=")[1];
                            deleteScore(username, score, recordTime);
                        }
                        else formatError = true;
                        break;
                    }
                    case "delAll" : {
                        deleteAllScore(username);
                        break;
                    }
                    default:
                }

                if(formatError) errorMessage = "请求格式错误";
                else state = "success";
            }
            else {
                errorMessage = "请求格式错误";
            }

            String responseData;
            if("get".equals(requestType)) {
                responseData = "{\n" +
                        "  \"state\": \"" + state + "\",\n" +
                        "  \"message\": \"" + errorMessage + "\",\n" +
                        "  \"scores\": " + queryResult + "\n" +
                        "}";
            }
            else {
                responseData = "{\n" +
                        "  \"state\": \"" + state + "\",\n" +
                        "  \"message\": \"" + errorMessage + "\"\n" +
                        "}";
            }
            // 发送响应（200表示成功，第二个参数是响应数据长度）
            exchange.sendResponseHeaders(200, responseData.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseData.getBytes(StandardCharsets.UTF_8));
            os.close(); // 必须关闭流，否则客户端会阻塞
        }
    }

    // 创建用户密码表
    public static void createAuthTable() {
        String sql = "CREATE TABLE IF NOT EXISTS auth ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "password TEXT NOT NULL )";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("用户密码表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 创建用户分数表
    public static void createScoreTable() {
        String sql = "CREATE TABLE IF NOT EXISTS score ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "score Integer NOT NULL, "
                + "recordTime TEXT NOT NULL )";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("分数表创建成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void register(String username, String password) {
        String sql = "INSERT INTO auth(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("用户 " + username + " 添加成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean login(String username, String password) {
        String sql = "SELECT * FROM auth WHERE username = ? ";
        boolean result = false;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String pwd = rs.getString("password");
                if(pwd.equals(password)) {
                    result = true;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void insertScore(String username, int score) {
        String sql = "INSERT INTO score(username, score, recordTime) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            pstmt.setString(3, time);
            pstmt.executeUpdate();
            System.out.println("用户 " + username + " 分数添加成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteScore(String username, int score, String recordTime) {
        String sql = "DELETE FROM score WHERE username = ? AND score = ? AND recordTime = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.setString(3, recordTime);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("用户 " + username + " recordTime: " + recordTime + " 已删除");
            } else {
                System.out.println("未找到记录: " + username + " recordTime: " + recordTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAllScore(String username) {
        String sql = "DELETE FROM score WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("用户 " + username + " 所有记录已删除");
            } else {
                System.out.println("未找到记录: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询用户所有记录
    public static String queryAllScore(String username) {
        StringBuilder listString = new StringBuilder("[ ");
        String sql = "SELECT * FROM score WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listString.append(String.format("{\"username\":\"%s\", \"score\":%s, \"recordTime\":\"%s\"}, ",
                        rs.getString("username"), rs.getInt("score"), rs.getString("recordTime")));
            }
            if(listString.length() > 2) listString.setLength(listString.length()-2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listString.append(" ]");
        return listString.toString();
    }

    private static Room allocateRoom(String username, String mode) {
        boolean flag = false;
        Room allocatedRoom = null;
        for(Room room : rooms) {
            if(!room.isFull() && (room.getMode() == null || room.getMode().equals(mode))) {
                allocatedRoom = room;
                room.addUser(username);
                room.setMode(mode);
                flag = true;
                break;
            }
        }

        if(!flag){
            allocatedRoom = new Room();
            rooms.add(allocatedRoom);
            allocatedRoom.addUser(username);
            allocatedRoom.setMode(mode);
        }
        return allocatedRoom;
    }
}