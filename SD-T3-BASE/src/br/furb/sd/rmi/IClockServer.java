package br.furb.sd.rmi;

import java.time.LocalTime;

public interface IClockServer {

    int getDiffTime(LocalTime time);

    void setDiffTime(int seconds);

}
