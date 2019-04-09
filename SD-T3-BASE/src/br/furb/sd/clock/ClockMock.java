package br.furb.sd.clock;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ClockMock {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private static LocalTime timeMock = getRandomTime();

    static {
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            LocalTime time = getLocalTime();

            LocalTime newTime = time.plusSeconds(1);
            setLocalTime(newTime);

            System.out.println("Local time: " + newTime);
        }, 1, 1, TimeUnit.SECONDS);
    }

    private ClockMock() {

    }

    private static final LocalTime getRandomTime() {
        int hour = getRandomInt(0, 23);
        int minute = getRandomInt(0, 59);
        int second = getRandomInt(0, 59);

        return LocalTime.of(hour, minute, second, 0);
    }

    private static final int getRandomInt(int min, int max) {
        Random r = new Random();
        int value = -1;
        while (value < min || value > max) {
            value = r.nextInt(max);
        }
        return value;
    }

    public static synchronized LocalTime getLocalTime() {
        return LocalTime.from(timeMock);
    }

    public static synchronized void setLocalTime(LocalTime time) {
        timeMock = time;
    }

    public static void init() {
    }
}
