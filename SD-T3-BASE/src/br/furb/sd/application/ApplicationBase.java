package br.furb.sd.application;

import br.furb.sd.clock.ClockMock;

public abstract class ApplicationBase {

    public ApplicationBase() {
        printHeader();
        ClockMock.init();
    }

    public abstract void run();

    public static void printHeader() {
        System.out.println("===========================================");
        System.out.println("=  FURB - 2019/1 - SISTEMAS DISTRIBUIDOS  =");
        System.out.println("===========================================");
        System.out.println("= ARIEL ADONAI SOUZA                      =");
        System.out.println("= GABRIEL CASTELLANI                      =");
        System.out.println("= GABRIEL LUIS FERNANDO DE SOUZA          =");
        System.out.println("===========================================");
    }
}
