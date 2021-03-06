package com.herojeff.sulchedule.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class RecordMonth {
    private int year;
    private int month;
    private boolean enable_daysOfMonth = false;
    private boolean enable_streakOfMonth = false;
    private boolean enable_caloriesOfMonth = false;
    private boolean enable_totalExpense = false;
    private int goal_daysOfMonth = 0;
    private int goal_streakOfMonth = 0;
    private int goal_caloriesOfMonth = 0;
    private int goal_totalExpense = 0;
    private boolean first_launch_of_month = true;
    private ArrayList<RecordDay> recordDays = new ArrayList<>();

    public RecordMonth(int year, int month) {
        this.year = year;
        this.month = month;

        previousMonthGoalMigration(year, month);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    void previousMonthGoalMigration(int source_year, int source_month) {
        //set source year, month
        if (source_month <= 1) {
            source_year--;
            source_month = 12;
        } else {
            source_month--;
        }

        //migration process
        if (SharedResources.recordMonth_exists(source_year, source_month)) {
            RecordMonth source_recordMonth = SharedResources.getRecordMonth(source_year, source_month);

            enable_daysOfMonth = source_recordMonth.isEnable_daysOfMonth();
            enable_caloriesOfMonth = source_recordMonth.isEnable_caloriesOfMonth();
            enable_streakOfMonth = source_recordMonth.isEnable_streakOfMonth();
            enable_totalExpense = source_recordMonth.isEnable_totalExpense();

            goal_daysOfMonth = source_recordMonth.getGoal_daysOfMonth();
            goal_caloriesOfMonth = source_recordMonth.getGoal_caloriesOfMonth();
            goal_streakOfMonth = source_recordMonth.getGoal_streakOfMonth();
            goal_totalExpense = source_recordMonth.getGoal_totalExpense();
        } else {
            //leave everything as it is
        }
    }

    public MonthlyBest getMonthlyBest() {
        MonthlyBest monthlyBest = new MonthlyBest();
        //scanner
        HashMap<Integer, Integer> monthlyBestDrink = new HashMap<>();
        HashMap<String, Integer> monthlyBestWhom = new HashMap<>();
        HashMap<String, Integer> monthlyBestLoc = new HashMap<>();
        for (RecordDay recordDay : recordDays) {
            //consolidation
            if (recordDay.getSul_list().size() != 0) {
                for (int i : recordDay.getSul_list().keySet()) {
                    if (SharedResources.getSul(i).isSul_enabled()) {
                        int a;
                        try {
                            a = monthlyBestDrink.get(i);
                        } catch (Exception e) {
                            monthlyBestDrink.put(i, 0);
                            a = 0;
                        }
                        int b = recordDay.getCertain_sul_count(i);
                        monthlyBestDrink.remove(i);
                        monthlyBestDrink.put(i, a + b);
                    }
                }
            }

            for (String i : recordDay.getFriend_list()) {
                int a;
                try {
                    a = monthlyBestWhom.get(i);
                } catch (Exception e) {
                    monthlyBestWhom.put(i, 0);
                    a = 0;
                }
                monthlyBestWhom.remove(i);
                monthlyBestWhom.put(i, a + 1);
            }
            for (String i : recordDay.getLocation_list()) {
                int a;
                try {
                    a = monthlyBestLoc.get(i);
                } catch (Exception e) {
                    monthlyBestWhom.put(i, 0);
                    a = 0;
                }
                monthlyBestLoc.remove(i);
                monthlyBestLoc.put(i, a + 1);
            }
        }
        //set value for monthlybest
        for (int i : monthlyBestDrink.keySet()) {
            if (monthlyBest.drink_count < monthlyBestDrink.get(i)) {
                monthlyBest.drink_count = monthlyBestDrink.get(i);
                monthlyBest.drink_index = i;
                monthlyBest.drink_expense = SharedResources.getSul(i).sul_price * monthlyBest.drink_count;
                monthlyBest.drink_calorie = SharedResources.getSul(i).sul_calorie * monthlyBest.drink_count;
            }
        }
        for (String i : monthlyBestWhom.keySet()) {
            if (monthlyBest.whom_count < monthlyBestWhom.get(i)) {
                monthlyBest.whom_count = monthlyBestWhom.get(i);
                for (RecordDay recordDay : recordDays) {
                    if (recordDay.getFriend_list().contains(i)) {
                        monthlyBest.whom_calorie += recordDay.getCalorie();
                        monthlyBest.whom_expense += recordDay.getExpense();
                    }
                }
                monthlyBest.whom = i;
            }
        }
        for (String i : monthlyBestLoc.keySet()) {
            if (monthlyBest.loc_count < monthlyBestLoc.get(i)) {
                monthlyBest.loc_count = monthlyBestLoc.get(i);
                for (RecordDay recordDay : recordDays) {
                    if (recordDay.getLocation_list().contains(i)) {
                        monthlyBest.loc_calorie += recordDay.getCalorie();
                        monthlyBest.loc_expense += recordDay.getExpense();
                    }
                }
                monthlyBest.loc = i;
            }
        }

        return monthlyBest;
    }

    public void cleanup() {
        for (RecordDay recordDay : recordDays) {
            if (recordDay.getCalorie() == 0 && recordDay.getExpense() == 0 && recordDay.getLocation_list().size() == 0 && recordDay.getFriend_list().size() == 0) {
                boolean removal_flag = true;
                for (int i : recordDay.getSul_list().keySet()) {
                    if (recordDay.getCertain_sul_count(i) != 0) {
                        removal_flag = false;
                    }
                }
                if (removal_flag) {
                    System.out.println("removing recordDay");
                    recordDays.remove(recordDay);
                }
            }
        }
    }

    public ArrayList<RecordDay> getRecordDays() {
        return recordDays;
    }

    public void setRecordDays(ArrayList<RecordDay> recordDays) {
        this.recordDays = recordDays;
    }

    public boolean isEnable_daysOfMonth() {
        return enable_daysOfMonth;
    }

    public void setEnable_daysOfMonth(boolean enable_daysOfMonth) {
        this.enable_daysOfMonth = enable_daysOfMonth;
    }

    public boolean isEnable_streakOfMonth() {
        return enable_streakOfMonth;
    }

    public void setEnable_streakOfMonth(boolean enable_streakOfMonth) {
        this.enable_streakOfMonth = enable_streakOfMonth;
    }

    public boolean isEnable_caloriesOfMonth() {
        return enable_caloriesOfMonth;
    }

    public void setEnable_caloriesOfMonth(boolean enable_caloriesOfMonth) {
        this.enable_caloriesOfMonth = enable_caloriesOfMonth;
    }

    public boolean isEnable_totalExpense() {
        return enable_totalExpense;
    }

    public void setEnable_totalExpense(boolean enable_totalExpense) {
        this.enable_totalExpense = enable_totalExpense;
    }

    public int getGoal_daysOfMonth() {
        return goal_daysOfMonth;
    }

    public void setGoal_daysOfMonth(int goal_daysOfMonth) {
        this.goal_daysOfMonth = goal_daysOfMonth;
    }

    public int getGoal_streakOfMonth() {
        return goal_streakOfMonth;
    }

    public void setGoal_streakOfMonth(int goal_streakOfMonth) {
        this.goal_streakOfMonth = goal_streakOfMonth;
    }

    public int getGoal_caloriesOfMonth() {
        return goal_caloriesOfMonth;
    }

    public void setGoal_caloriesOfMonth(int goal_caloriesOfMonth) {
        this.goal_caloriesOfMonth = goal_caloriesOfMonth;
    }

    public int getGoal_totalExpense() {
        return goal_totalExpense;
    }

    public void setGoal_totalExpense(int goal_totalExpense) {
        this.goal_totalExpense = goal_totalExpense;
    }

    public boolean isFirst_launch_of_month() {
        return first_launch_of_month;
    }

    public void setFirst_launch_of_month(boolean first_launch_of_month) {
        this.first_launch_of_month = first_launch_of_month;
    }

    public void disableFirst_launch_of_month() {
        this.first_launch_of_month = false;
    }

    public double getTrafficSignal() { //if -1.0, nothing is enabled
        double return_value = -1.0;
        boolean[] enabled = new boolean[]{
                isEnable_daysOfMonth(), isEnable_streakOfMonth(), isEnable_totalExpense(), isEnable_caloriesOfMonth()
        };
        double[] bar_t = new double[]{
                goalStat_daysOfMonth(), goalStat_streakOfMonth(), goalStat_totalExpense(), goalStat_caloriesOfMonth()
        };
        for (int i = 0; i < 4; i++) {
            if (enabled[i]) {
                return_value = bar_t[i] > return_value ? bar_t[i] : return_value;
            }
        }
        return return_value;
    }

    public int stat_daysOfMonth() {
        ArrayList<RecordDay> arr = SharedResources.getMonthlyRecordDayArray(year, month);
        return arr.size();
    }

    public int stat_streakOfMonth() {
        ArrayList<RecordDay> arr = SharedResources.getMonthlyRecordDayArray(year, month);


        //is arr.size dynamically refreshed?


        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).isTodayEmpty()) {
                arr.remove(i);
                i--;
            }
        }
        Collections.sort(arr, new DescendingRecordDay());

        int max = 0, new_max = 1;
        ArrayList<Integer> temp_array = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            temp_array.add(arr.get(i).getDay());
            max = 1;
        }
        int i = 0;
        while (i + 1 < temp_array.size()) {
            if (temp_array.get(i) + 1 == temp_array.get(i + 1)) {
                new_max++;
                if (new_max > max) {
                    max = new_max;
                }
            } else {
                new_max = 1;
            }
            i++;
        }

        return max;
    }

    public int stat_caloriesOfMonth() {
        return getTotalCalorie();
    }

    public int stat_totalExpense() {
        return getTotalExpense();
    }

    public double goalStat_daysOfMonth() {
        if (enable_daysOfMonth) {
            double result = 0.0;
            ArrayList<RecordDay> arr = SharedResources.getMonthlyRecordDayArray(year, month);
            if (arr.size() == 0) {
                return 0.0;
            } else if (goal_daysOfMonth == 0) {
                return 1.0;
            } else {
                return (double) arr.size() / (double) goal_daysOfMonth;
            }
        } else {
            return 0.0;
        }
    }

    public double goalStat_streakOfMonth() {
        if (enable_streakOfMonth) {
            double result = 0.0;
            ArrayList<RecordDay> arr = SharedResources.getMonthlyRecordDayArray(year, month);


            //is arr.size dynamically refreshed?


            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).isTodayEmpty()) {
                    arr.remove(i);
                    i--;
                }
            }
            Collections.sort(arr, new DescendingRecordDay());

            int max = 0, new_max = 1;
            ArrayList<Integer> temp_array = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                temp_array.add(arr.get(i).getDay());
                max = 1;
            }
            int i = 0;
            while (i + 1 < temp_array.size()) {
                if (temp_array.get(i) + 1 == temp_array.get(i + 1)) {
                    new_max++;
                    if (new_max > max) {
                        max = new_max;
                    }
                } else {
                    new_max = 1;
                }
                i++;
            }

            if (max == 0) {
                return 0.0;
            } else if (goal_streakOfMonth == 0) {
                return 1.0;
            } else {
                return (double) max / (double) goal_streakOfMonth;
            }
        } else {
            return 0.0;
        }
    }

    public double goalStat_caloriesOfMonth() {
        return (double) getTotalCalorie() / (double) goal_caloriesOfMonth;
    }

    public double goalStat_totalExpense() {
        return (double) getTotalExpense() / (double) goal_totalExpense;
    }

    public int getTotalExpense() {
        int return_int = 0;
        for (RecordDay recordDay : recordDays) {
            return_int += recordDay.getExpense();
        }
        return return_int;
    }

    public int getTotalCalorie() {
        int return_int = 0;
        for (RecordDay recordDay : recordDays) {
            return_int += recordDay.getCalorie();
        }
        return return_int;
    }

    public HashMap<Integer, Integer> getMonthlySulCompilation() {
        HashMap<Integer, Integer> returnMap = new HashMap<>();

        for (RecordDay recordDay : recordDays) {
            HashMap<Integer, Integer> recordDayTempMap = recordDay.getSul_list();
            for (int i : recordDayTempMap.keySet()) {
                if (!returnMap.containsKey(i)) {
                    returnMap.put(i, 0);
                }
                int setValue = returnMap.get(i);
                setValue += recordDayTempMap.get(i);
                returnMap.put(i, setValue);
            }
        }

        return returnMap;
    }

    public HashMap<String, Integer> getMonthlyFriendCompilation() {
        HashMap<String, Integer> returnMap = new HashMap<>();

        for (RecordDay recordDay : recordDays) {
            recordDay.getFriend_list();
            for (String i : recordDay.getFriend_list()) {
                if (!returnMap.containsKey(i)) {
                    returnMap.put(i, 0);
                }
                int setValue = returnMap.get(i);
                setValue++;
                returnMap.put(i, setValue);
            }
        }

        return returnMap;
    }

    public HashMap<String, Integer> getMonthlyLocationCompilation() {
        HashMap<String, Integer> returnMap = new HashMap<>();

        for (RecordDay recordDay : recordDays) {
            for (String i : recordDay.getLocation_list()) {
                if (!returnMap.containsKey(i)) {
                    returnMap.put(i, 0);
                }
                int setValue = returnMap.get(i);
                setValue++;
                returnMap.put(i, setValue);
            }
        }

        return returnMap;
    }

    public boolean checkEligibleRemoveAdEligible() {
        if (!enable_daysOfMonth && !enable_streakOfMonth && !enable_caloriesOfMonth && !enable_totalExpense) {
            return false;
        }
        if (enable_totalExpense && goalStat_totalExpense() >= 1) {
            return false;
        }
        if (enable_streakOfMonth && goalStat_streakOfMonth() >= 1) {
            return false;
        }
        if (enable_caloriesOfMonth && goalStat_caloriesOfMonth() >= 1) {
            return false;
        }
        if (enable_daysOfMonth && goalStat_daysOfMonth() >= 1) {
            return false;
        }
        return true;
    }

    public class MonthlyBest {
        public int drink_index = -1;
        public int drink_count = 0;
        public int drink_expense = 0;
        public int drink_calorie = 0;
        public String whom;
        public int whom_count = 0;
        public int whom_expense = 0;
        public int whom_calorie = 0;
        public String loc;
        public int loc_count = 0;
        public int loc_expense = 0;
        public int loc_calorie = 0;
    }
}

class DescendingRecordDay implements Comparator<RecordDay> {
    @Override
    public int compare(RecordDay o1, RecordDay o2) {
        return o1.getDay() - o2.getDay();
    }
}