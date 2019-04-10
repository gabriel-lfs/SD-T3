package br.furb.sb.rmi.server;

import br.furb.sd.clock.ClockMock;
import br.furb.sd.clock.ClockUtils;
import br.furb.sd.rmi.IClockServer;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;

public class ClockServerImpl extends UnicastRemoteObject implements IClockServer {

    protected ClockServerImpl() throws RemoteException {
    }

    @Override
    public int getDiffTime(LocalTime time) {
        try {
            System.out.println("Sending diff local time to [" + getClientHost() + "]");

            LocalTime localTime = ClockMock.getInstance().getLocalTime();
            return ClockUtils.getDiffTime(localTime, time);
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setDiffTime(int seconds) {
        try {
            System.out.println("Received new diff local time [" + seconds + "] from [" + getClientHost() + "]");

            LocalTime time = ClockMock.getInstance().getLocalTime();
            LocalTime newTime = time.plusSeconds(seconds);
            ClockMock.getInstance().setLocalTime(newTime);
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
