package com.herojeff.sulchedule;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.herojeff.sulchedule.data.CustomColor;
import com.herojeff.sulchedule.data.CustomDayManager;
import com.herojeff.sulchedule.data.RecordMonth;
import com.herojeff.sulchedule.data.SharedResources;

import java.util.ArrayList;

public class TrafficGoalRecyclerViewAdapter extends RecyclerView.Adapter<TrafficGoalRecyclerViewAdapter.TrafficGoalRecyclerViewHolder> {

    int year;
    int month;
    RecordMonth recordMonth;

    boolean[] enabled;
    String[] left_bottom;
    int[] right_top;
    String[] left_top;
    double[] bar_t;

    ArrayList<ArrayList<Integer>> spinner = new ArrayList<>();

    public TrafficGoalRecyclerViewAdapter() {
        year = CustomDayManager.getTodayYear();
        month = CustomDayManager.getTodayMonth();
        recordMonth = SharedResources.getRecordMonth(year, month);

        enabled = new boolean[]{
                recordMonth.isEnable_daysOfMonth(), recordMonth.isEnable_streakOfMonth(), recordMonth.isEnable_totalExpense(), recordMonth.isEnable_caloriesOfMonth()
        };
        bar_t = new double[]{
                recordMonth.goalStat_daysOfMonth(), recordMonth.goalStat_streakOfMonth(), recordMonth.goalStat_totalExpense(), recordMonth.goalStat_caloriesOfMonth()
        };
        left_top = new String[]{
                recordMonth.stat_daysOfMonth() + "일", recordMonth.stat_streakOfMonth() + "일", recordMonth.stat_totalExpense() + "원", recordMonth.stat_caloriesOfMonth() + "kcal"
        };
        right_top = new int[]{
                recordMonth.getGoal_daysOfMonth(), recordMonth.getGoal_streakOfMonth(), recordMonth.getGoal_totalExpense(), recordMonth.getGoal_caloriesOfMonth()
        };
        left_bottom = new String[]{
                CustomDayManager.getTodayMonth() + "월 음주 일수", CustomDayManager.getTodayMonth() + "월 연이은 음주 일수", "지출액", "열량"
        };
    }

    @NonNull
    @Override
    public TrafficGoalRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_goal_item, viewGroup, false);

        return new TrafficGoalRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull TrafficGoalRecyclerViewHolder trafficGoalRecyclerViewHolder, final int i) {
        if (enabled[i]) {
            trafficGoalRecyclerViewHolder.text_right_top.setText(String.valueOf(right_top[i]));
            trafficGoalRecyclerViewHolder.text_right_top.setTextColor(CustomColor.color_white);
            trafficGoalRecyclerViewHolder.itemView.post(new Runnable() {
                @Override
                public void run() {
                    int cellWidth = trafficGoalRecyclerViewHolder.graph_full_bar.getWidth();// this will give you cell width dynamically
                    trafficGoalRecyclerViewHolder.graph_overlay.setLayoutParams(new RelativeLayout.LayoutParams((int) (cellWidth * bar_t[i]), trafficGoalRecyclerViewHolder.graph_full_bar.getHeight()));
                }
            });
        } else {
            trafficGoalRecyclerViewHolder.text_right_top.setText("");
        }
        trafficGoalRecyclerViewHolder.text_left_top.setText(left_top[i]);
        trafficGoalRecyclerViewHolder.text_left_bottom.setText(left_bottom[i]);
        trafficGoalRecyclerViewHolder.text_right_bottom.setText("한도");

        if (bar_t[i] >= 1.0) {
            trafficGoalRecyclerViewHolder.graph_overlay.setImageTintList(ColorStateList.valueOf(CustomColor.color_traffic_red));
        } else if (bar_t[i] >= 0.7) {
            trafficGoalRecyclerViewHolder.graph_overlay.setImageTintList(ColorStateList.valueOf(CustomColor.color_traffic_yellow));
        } else {
            trafficGoalRecyclerViewHolder.graph_overlay.setImageTintList(ColorStateList.valueOf(CustomColor.color_traffic_green));
        }
    }


    @Override
    public void onViewAttachedToWindow(@NonNull TrafficGoalRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class TrafficGoalRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView text_left_top;
        EditText text_right_top;
        TextView text_left_bottom;
        TextView text_right_bottom;
        ImageView graph_overlay;
        ImageView graph_full_bar;

        public TrafficGoalRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            text_left_top = itemView.findViewById(R.id.text_left_top);
            text_right_top = itemView.findViewById(R.id.text_right_top);
            text_left_bottom = itemView.findViewById(R.id.text_left_bottom);
            text_right_bottom = itemView.findViewById(R.id.text_right_bottom);
            graph_overlay = itemView.findViewById(R.id.graph_overlay);
            graph_full_bar = itemView.findViewById(R.id.graph_full_bar);


        }
    }

    void initSpinnerValue(){
        spinner.add(0, new ArrayList<Integer>());
        spinner.add(1, new ArrayList<Integer>());
        spinner.add(2, new ArrayList<Integer>());
        spinner.add(3, new ArrayList<Integer>());

        //0
        for (int i = -1; i<=CustomDayManager.getLastDayOfMonth(CustomDayManager.getMonth());i++){
            spinner.get(0).add(i);
        }

        //1
        for (int i = -1; i<=CustomDayManager.getLastDayOfMonth(CustomDayManager.getMonth());i++){
            spinner.get(1).add(i);
        }

        //2
        for (int i = -1; i<=19;i++){
            spinner.get(2).add(i*5000);
        }
        for (int i = 2; i<=10;i++){
            spinner.get(2).add(i*10000);
        }
        //5000씩, 100000까지, 50000씩, 500000까지

        //3
        for (int i = -1; i<=19;i++){
            spinner.get(3).add(i*50);
        }
        for (int i = 10; i<=49;i++){
            spinner.get(3).add(i*100);
        }
        for (int i = 10; i<=19;i++){
            spinner.get(3).add(i*500);
        }
        for (int i = 2; i<=20;i++){
            spinner.get(3).add(i*5000);
        }
        //50씩, 1000까지, 100씩, 5000까지, 500씩, 10000까지, 5000씩 100000까지
    }
}
