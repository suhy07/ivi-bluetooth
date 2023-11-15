package com.jancar.bluetooth.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static String formatTime(Date time) {

        long currentTimeMillis = System.currentTimeMillis();
        long timeMillis = time.getTime();
        long oneDayMillis = 24 * 60 * 60 * 1000;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedTime = sdf.format(time);

        if (currentTimeMillis - timeMillis < oneDayMillis) {
            if (currentTimeMillis - timeMillis < 60 * 60 * 1000) {
                return "刚刚";
            } else if (currentTimeMillis - timeMillis < 12 * 60 * 60 * 1000) {
                return "上午 " + formattedTime;
            } else {
                return "下午 " + formattedTime;
            }
        } else if (currentTimeMillis - timeMillis < 2 * oneDayMillis) {
            return "昨天 " + formattedTime;
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        }
    }


    public static String formatTime(String inputTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
            Date date = inputFormat.parse(inputTime);
            Calendar calendar = Calendar.getInstance();

            long currentTimeMillis = System.currentTimeMillis();
            long timeMillis = date.getTime();
            long timeDiffMillis = currentTimeMillis - timeMillis;

            if (timeDiffMillis < 60 * 1000) {
                return "刚刚";
            } else if (timeDiffMillis < 60 * 60 * 1000) {
                long minutesAgo = timeDiffMillis / (60 * 1000);
                return minutesAgo + "分钟前";
            } else if (timeDiffMillis < 2 * 60 * 60 * 1000) {
                return "1小时前";
            } else if (timeDiffMillis < 3 * 60 * 60 * 1000) {
                return "2小时前";
            } else if (timeDiffMillis < 4 * 60 * 60 * 1000) {
                return "3小时前";
            } else if (timeDiffMillis < 5 * 60 * 60 * 1000) {
                return "4小时前";
            } else if (isSameDay(calendar, timeMillis)) {
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                if (hourOfDay < 12) {
                    return "上午 " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
                } else {
                    return "下午 " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
                }
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                if (isSameDay(calendar, timeMillis)) {
                    return "昨天";
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    if (isSameDay(calendar, timeMillis)) {
                        return "前天";
                    } else {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                        return outputFormat.format(date);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return inputTime; // 解析失败时返回原始字符串
        }
    }

    public static String formatAccurateTime(String inputTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault());
            Date date = inputFormat.parse(inputTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return inputTime; // 解析失败时返回原始字符串
        }
    }

    private static boolean isSameDay(Calendar cal, long timeMillis) {
        cal.setTimeInMillis(timeMillis);
        int year = cal.get(Calendar.YEAR);
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return year == currentYear && dayOfYear == currentDayOfYear;
    }
}
