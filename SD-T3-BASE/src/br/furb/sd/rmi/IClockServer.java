package br.furb.sd.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;

public interface IClockServer extends Remote {

    int getDiffTime(LocalTime time) throws RemoteException;

    void setDiffTime(int seconds) throws RemoteException;

}
