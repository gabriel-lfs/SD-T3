package br.furb.sd.clock;

import java.time.LocalTime;

public abstract class ClockUtils {

    public static int getDiffTime(LocalTime time1, LocalTime time2) {
        return getTime(time2) - getTime(time1);
    }

    public static int getTimeApplingDiff(LocalTime time1, int diff) {
//        int restHour = diff % (24 * 60);
//        int hour = (diff / (24 * 60)) - restHour;
//        diff -= (restHour + hour);
//
//        int restMinute = diff % 60;
//        int minute = (diff / 60) - restMinute;
//        diff -= (restMinute + minute);
//
//        int seconds = diff;
//
//        return
        return -1;
    }

    public static int getTime(LocalTime time) {
        return time.getSecond() + (time.getMinute() * 60) + (time.getHour() * 24 * 60);
    }
}
