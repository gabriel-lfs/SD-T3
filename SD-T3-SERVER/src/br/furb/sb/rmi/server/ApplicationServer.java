package br.furb.sb.rmi.server;

import br.furb.sd.application.ApplicationBase;
import br.furb.sd.clock.ClockMock;
import br.furb.sd.rmi.IClockServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer extends ApplicationBase {

    public static void main(String[] args) {
        new ApplicationServer().run();
    }

    @Override
    public void run() {
        System.out.println("Starting server");

        try {
            System.out.println("Getting local RMI registry");
            Registry registry = LocateRegistry.getRegistry();

            String bindName = IClockServer.class.getSimpleName();

            System.out.println("Rebinding [" + bindName + "]");
            registry.rebind(bindName, new ClockServerImpl());
            System.out.println("[" + bindName + "] rebinded");

            System.out.println("Server started!");
        } catch (Exception e) {
            System.out.println("Failure on server register");
            e.printStackTrace();

            System.exit(1);
        }
    }
}
