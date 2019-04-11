package br.furb.sb.rmi.client;

import br.furb.sd.clock.ClockMock;
import br.furb.sd.clock.ClockUtils;
import br.furb.sd.rmi.IClockServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClockClient {

    private static final String THIS = "THIS_HOST";
    private final List<String> hosts;
    private final Map<String, Registry> registryCache = new HashMap<>();
    private final Map<String, IClockServer> clockServerCache = new HashMap<>();

    public ClockClient(List<String> hosts) {
        this.hosts = Collections.unmodifiableList(hosts);
    }

    public void equalizeTimes() {
        System.out.println("Equalizing times");

        final Map<String, Integer> timesByHosts = getTimesByHosts();
        final int average = getAverageTimes(timesByHosts);

        timesByHosts.entrySet().stream().forEach(entry -> {
            String host = entry.getKey();
            Integer time = entry.getValue();

            int diffTime = -(time - average);

            if (THIS.equals(host)) {
                LocalTime localTime = ClockMock.getInstance().getLocalTime();
                LocalTime newTime = localTime.plusSeconds(diffTime);
                ClockMock.getInstance().setLocalTime(newTime);
            } else {
                setDiffTime(host, diffTime);
            }
        });

        System.out.println("Equalizeted!");
    }

    private int getAverageTimes(Map<String, Integer> timesByHosts) {
        return (int) timesByHosts.values().parallelStream() //
                .mapToDouble(Integer::doubleValue) //
                .average() //
                .orElse(0d);
    }

    private Map<String, Integer> getTimesByHosts() {
        Map<String, Integer> timesByHosts = new HashMap<>();

        hosts.parallelStream().forEach(host -> {
            int diffTime = getDiffTime(host);
            LocalTime time = ClockMock.getInstance().getLocalTime();

            LocalTime timeDiff = time.plusSeconds(diffTime);

            synchronized (timesByHosts) {
                timesByHosts.put(host, ClockUtils.getSeconds(timeDiff));
            }
        });

        LocalTime time = ClockMock.getInstance().getLocalTime();
        timesByHosts.put(THIS, ClockUtils.getSeconds(time));

        return timesByHosts;
    }

    private void setDiffTime(final String host, final int average) {
        try {
            IClockServer clockServer = getClockServer(host);
            clockServer.setDiffTime(average);
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

    public void printHostTimes() {
        System.out.println("Getting times...");
        final Map<String, Integer> timesByHosts = getTimesByHosts();

        timesByHosts.forEach((host, time) -> {
            LocalTime time0 = LocalTime.of(0, 0, 0);
            LocalTime timeHost = time0.plusSeconds(time);

            System.out.println("[" + host + "] - " + timeHost);
        });
    }
}
