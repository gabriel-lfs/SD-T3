package br.furb.sb.rmi.server;

import br.furb.sd.clock.ClockMock;

public class ApplicationServer {

    public static void main(String[] args) {
        ClockMock.getLocalTime();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
