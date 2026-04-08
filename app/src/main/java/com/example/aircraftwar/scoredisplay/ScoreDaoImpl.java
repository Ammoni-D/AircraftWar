package com.example.aircraftwar.scoredisplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDaoImpl implements ScoreDao {

    // ===================== 实验要求：SQLite 核心 =====================
    private SQLiteDatabase db;
    private ScoreDbHelper dbHelper;

    public ScoreDaoImpl(Context context) {
        dbHelper = new ScoreDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // 建表类（内部类，不破坏你原有结构）
    private static class ScoreDbHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "score_db";
        private static final int VERSION = 1;
        private static final String TABLE_NAME = "score_record";

        public ScoreDbHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + TABLE_NAME + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT,"
                    + "score INTEGER,"
                    + "recordTime TEXT)";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    // ===================== 新增分数（ContentValues） =====================
    @Override
    public void addScore(Score score) {
        ContentValues values = new ContentValues();
        values.put("username", score.getUsername());
        values.put("score", score.getScore());
        values.put("recordTime", score.getRecordTime());

        db.insert("score_record", null, values);
    }

    // ===================== 查询所有（Cursor + 排序） =====================
    @Override
    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();

        Cursor cursor = db.query("score_record",
                null, null, null, null, null,
                "score DESC");

        while (cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            int scoreValue = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            String recordTime = cursor.getString(cursor.getColumnIndexOrThrow("recordTime"));

            scores.add(new Score(username, scoreValue, recordTime));
        }
        cursor.close();
        return scores;
    }

    // ===================== 删除 =====================
    @Override
    public void deleteScore(Score score) {
        db.delete("score_record",
                "username=? AND score=? AND recordTime=?",
                new String[]{
                        score.getUsername(),
                        String.valueOf(score.getScore()),
                        score.getRecordTime()
                });
    }

    // 清空所有
    public void clearAllScores() {
        db.delete("score_record", null, null);
    }
}