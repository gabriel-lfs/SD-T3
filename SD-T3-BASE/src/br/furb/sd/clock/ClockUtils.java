package br.furb.sd.clock;

import java.time.LocalTime;

public abstract class ClockUtils {

    public static int getDiffTime(LocalTime time1, LocalTime time2) {
        return getSeconds(time1) - getSeconds(time2);
    }

    public static int getSeconds(LocalTime time) {
        return time.getSecond() + (time.getMinute() * 60) + (time.getHour() * 60 * 60);
    }
}
