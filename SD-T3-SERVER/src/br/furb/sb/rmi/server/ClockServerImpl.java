package br.furb.sb.rmi.server;

import br.furb.sd.rmi.IClockServer;

import java.time.LocalTime;

public class ClockServerImpl implements IClockServer {

    @Override
    public int getTime() {
        LocalTime now = LocalTime.now();
        return (now.getHour() * 60) + now.getMinute();
    }

    @Override
    public void setTime(int time) {


    }
}
