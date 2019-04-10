package br.furb.sb.rmi.client;

import br.furb.sd.clock.ClockMock;
import br.furb.sd.rmi.IClockServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.util.*;

public class ClockClient {

    private final List<String> hosts;
    private final Map<String, Registry> registryCache = new HashMap<>();
    private final Map<String, IClockServer> clockServerCache = new HashMap<>();

    public ClockClient(List<String> hosts) {
        this.hosts = Collections.unmodifiableList(hosts);
    }

    public void equalizeTimes() {
        System.out.println("Equalizing times");

        final double average = hosts.parallelStream() //
                .map(this::getDiffTime) //
                .mapToDouble(Integer::doubleValue) //
                .average() //
                .orElse(0d);

        hosts.parallelStream().forEach(host -> setDiffTime(host, average));
    }

    private void setDiffTime(String host, final double average) {
        try {
            IClockServer clockServer = getClockServer(host);
            clockServer.setDiffTime((int) average);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Failure to set diff time to [" + host + "] [" + e.getMessage() + "]");
        }
    }

    private Integer getDiffTime(String host) {
        try {
            IClockServer clockServer = getClockServer(host);

            LocalTime localTime = ClockMock.getInstance().getLocalTime();
            return clockServer.getDiffTime(localTime);
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Failure to get diff time from [" + host + "] [" + e.getMessage() + "]");
        }
        return null;
    }

    private IClockServer getClockServer(String host) throws RemoteException, NotBoundException {
        Registry registry;
        synchronized (registryCache) {
            registry = registryCache.get(host);
        }
        if (registry == null) {
            registry = LocateRegistry.getRegistry(host);
            synchronized (registryCache) {
                registryCache.put(host, registry);
            }
        }

        IClockServer clockServer;
        synchronized (clockServerCache) {
            clockServer = clockServerCache.get(host);
        }
        if (clockServer == null) {
            clockServer = (IClockServer) registry.lookup(IClockServer.class.getSimpleName());
            synchronized (clockServerCache) {
                clockServerCache.put(host, clockServer);
            }
        }

        return clockServer;
    }
}
