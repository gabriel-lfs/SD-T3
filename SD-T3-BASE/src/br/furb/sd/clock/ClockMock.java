package br.furb.sd.clock;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClockMock {

    private static ClockMock INSTANCE;

    private final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private LocalTime timeMock = getRandomTime();
    private LocalTime newLocalTime = null;

    private ClockMock() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            if (newLocalTime != null) {
                this.timeMock = this.newLocalTime;
                this.newLocalTime = null;
            } else {
                LocalTime newTime = getLocalTime().plusSeconds(1);
                this.timeMock = newTime;
            }

            System.out.println("Local time: " + getLocalTime());
        }, 1, 1, TimeUnit.SECONDS);

        System.out.println("ClockMock started!");
    }

    public static ClockMock getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClockMock();
        }
        return INSTANCE;
    }

    public static void init() {
        getInstance();
    }

    public synchronized LocalTime getLocalTime() {
        return LocalTime.from(this.timeMock);
    }

    public synchronized void setLocalTime(LocalTime time) {
        this.newLocalTime = time;
    }

    private final LocalTime getRandomTime() {
        int hour = getRandomInt(0, 23);
        int minute = getRandomInt(0, 59);
        int second = getRandomInt(0, 59);

        return LocalTime.of(hour, minute, second, 0);
    }

    private final int getRandomInt(int min, int max) {
        Random r = new Random();
        int value = -1;
        while (value < min || value > max) {
            value = r.nextInt(max);
        }
        return value;
    }
}
