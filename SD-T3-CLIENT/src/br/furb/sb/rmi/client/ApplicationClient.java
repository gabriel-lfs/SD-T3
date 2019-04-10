package br.furb.sb.rmi.client;

import br.furb.sd.clock.ClockMock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        ClockMock.init();

//        List<String> hosts = getHosts();
        List<String> hosts = Collections.singletonList("127.0.0.1");
        ClockClient client = new ClockClient(hosts);
        client.equalizeTimes();
    }

    private static List<String> getHosts() throws IOException, InterruptedException {
        System.out.println("Getting hosts address...");

        List<String> localAddresses = getLocalAddresses();
        String localAddress = getFilteredLocalAddress(localAddresses);
        System.out.println("Local address [" + localAddress + "]");

        System.out.println("Looking for reachable hosts...");
        List<String> possibleAddresses = getPossibleAddresses(localAddress);
        List<String> hostsReachable = getHostsReachable(possibleAddresses);

        System.out.println("Pinging on RMI port...");
        List<String> hosts = getHostsListenTCP(hostsReachable, 1099);

        if (!hosts.isEmpty()) {
            System.out.println("Hosts founded: ");
            hosts.forEach(host -> System.out.println(" - " + host));
        } else {
            System.out.println("No one host founded");
        }

        return hosts;
    }

    private static List<String> getHostsListenTCP(List<String> hosts, final int port) {
        List<String> validHosts = new ArrayList<>();

        final AtomicInteger max = new AtomicInteger(hosts.size());
        final AtomicInteger index = new AtomicInteger(0);

        int threads = (max.get() < 50) ? max.get() : 50;

        executeThreads(() -> {
            while (index.get() < max.get()) {
                String host;
                synchronized (hosts) {
                    host = hosts.get(index.getAndIncrement());
                }
                try {
                    Socket socket = new Socket(host, port);
                    socket.close();
                    synchronized (validHosts) {
                        validHosts.add(host);
                    }
                } catch (IOException e) {
                }
            }
        }, threads);

        return validHosts;
    }

    private static List<String> getHostsReachable(List<String> possibleAddresses) {
        final List<String> hostsReachable = new ArrayList<>();

        final AtomicInteger max = new AtomicInteger(possibleAddresses.size());
        final AtomicInteger index = new AtomicInteger(0);

        executeThreads(() -> {
            while (index.get() < max.get()) {
                int idxAddress = index.getAndIncrement();

                try {
                    String possibleAddress;
                    synchronized (possibleAddresses) {
                        possibleAddress = possibleAddresses.get(idxAddress);
                    }

                    InetAddress geek = InetAddress.getByName(possibleAddress);
                    if (geek.isReachable(500)) {
                        synchronized (hostsReachable) {
                            hostsReachable.add(possibleAddress);
                        }
                    }
                } catch (IOException e) {
                }
            }
        }, 50);

        return hostsReachable;
    }

    private static void executeThreads(Runnable r, int threads) {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i=0; i < threads; i++) {
            executorService.execute(r);
        }
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            executorService.shutdown();
        }
    }

    private static List<String> getPossibleAddresses(String localAddress) {
        String startAddress = localAddress.substring(0, localAddress.lastIndexOf('.') + 1);
        String endAddress = localAddress.substring(localAddress.lastIndexOf('.'));

        List<String> possibleAddresses = new ArrayList<>();
        for (int i=1; i < 255; i++) {
            String endPossibleAddress = String.valueOf(i);
            if (!endPossibleAddress.equals(endAddress)) {
                String possibleAddress = startAddress + endPossibleAddress;
                possibleAddresses.add(possibleAddress);
            }
        }
        return possibleAddresses;
    }

    private static String getFilteredLocalAddress(List<String> localAddresses) {
        Pattern pattern = Pattern.compile(".*\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");

        for (String localAddress : localAddresses) {
            Matcher matcher = pattern.matcher(localAddress);

            if (matcher.find() && !localAddress.startsWith("127.0.0.")) {
                return localAddress;
            }
        }
        return null;
    }

    private static List<String> getLocalAddresses() throws SocketException {
        List<String> localAddresses = new ArrayList<>();

        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();

                String hostAddress = i.getHostAddress();
                localAddresses.add(hostAddress);
            }
        }

        return localAddresses;
    }

}
