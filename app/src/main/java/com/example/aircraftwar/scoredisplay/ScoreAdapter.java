package com.example.aircraftwar.scoredisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aircraftwar.R;
import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private Context context;
    private List<Score> scoreList;
    private ScoreDao scoreDao;

    public ScoreAdapter(Context context, List<Score> scoreList, ScoreDao scoreDao) {
        this.context = context;
        this.scoreList = scoreList;
        this.scoreDao = scoreDao;
    }

    @Override
    public int getCount() {
        return scoreList.size();
    }

    @Override
    public Object getItem(int position) {
        return scoreList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
            holder = new ViewHolder();
            holder.tvUsername = convertView.findViewById(R.id.tv_username);
            holder.tvScore = convertView.findViewById(R.id.tv_score);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Score score = scoreList.get(position);
        holder.tvUsername.setText("玩家：" + score.getUsername());
        holder.tvScore.setText("分数：" + score.getScore());
        holder.tvTime.setText("时间：" + score.getRecordTime());

        holder.btnDelete.setOnClickListener(v -> {
            scoreDao.deleteScore(score);
            scoreList.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvUsername, tvScore, tvTime;
        Button btnDelete;
    }
}