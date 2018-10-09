package com.jewel.mplayer;

import java.math.BigDecimal;
import java.util.Formatter;
import java.util.Locale;

public class Utils {


    /**
     * @param timeMs 要转换为 %d:%02d:%02d 或 %02d:%02d String类型 的时间
     */
    public static String timeToString(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 根据count值得到1(count个数的0)的数。例如count为2，则得到100，count为1则得到10，count小于0则得到1
     *
     * @param count 1后补增的0个数
     * @return long
     */
    public static long zeroCountToNum(int count) {
        long num = 1;
        if (count <= 0) {
            return num;
        }
        for (int i = 1; i <= count; i++) {
            num *= 10;
        }
        return num;
    }

    /**
     * 获取比例
     *
     * @param current 分子
     * @param max     分母
     * @return 比例（0-100）
     */
    public static int percent(long current, long max) {
        return (int) (halfUp((float) current / max, 2) * zeroCountToNum(2));
    }

    /**
     * 获取四舍五入的值
     *
     * @param value    需要转换的数值
     * @param newScale 小数点保留位数
     * @return 四舍五入后的值。
     */
    public static float halfUp(float value, int newScale) {
        return getBigDecimal(value, newScale, BigDecimal.ROUND_HALF_UP);
    }

    private static float getBigDecimal(float value, int newScale, int roundingMode) {
        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.setScale(newScale, roundingMode).floatValue();
    }
}
