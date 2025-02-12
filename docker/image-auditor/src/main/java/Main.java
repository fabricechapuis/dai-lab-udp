import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
    static final String MULTICAST_ADDRESS = "224.0.0.1";

    static final int UDPPORT = 9904;
    static final int TCPPORT = 2205;

    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments expected");
        }
        
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        SharedData sharedData = new SharedData();

        Runnable UDPListener = () -> {runUDPListener(UDPPORT, group, sharedData);};

        Runnable TCPSender = () -> {try {
            TCPSender(TCPPORT, sharedData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }};

        Thread.startVirtualThread(UDPListener);
        Thread.startVirtualThread(TCPSender);
        runActiveMusiciansUpdater(sharedData);

    }
    
    private static void runUDPListener(int port, InetAddress group, SharedData sharedData) {
        try {
            System.out.println("UDPListner Listening on port " + port);
            InetSocketAddress groupAddress = new InetSocketAddress(group, port);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            
            while (true) {
                MulticastSocket socket = new MulticastSocket(port);
                socket.joinGroup(groupAddress, networkInterface);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + message);
                
                Map<String, String> map = new Gson().fromJson(message, new TypeToken<Map<String, String>>(){}.getType());
                
                if (!isMusicianAlreadyStored(sharedData, map)) {
                    sharedData.sharedMusicians.add(new Musician(map.get("uuid"), map.get("sound")));
                } else {
                    for (Musician musician : sharedData.sharedMusicians) {
                        if (musician.getUuid().equals(map.get("uuid"))) {
                            musician.updateTimeStamp();
                            musician.setActive(true);
                        }
                    }
                }
                socket.leaveGroup(groupAddress, networkInterface);
                socket.close();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean isMusicianAlreadyStored(SharedData sharedData, Map<String, String> map) {
        for (Musician musician : sharedData.sharedMusicians) {
            if (musician.getUuid().equals(map.get("uuid"))) {
                return true;
            }
        }
        return false;
    }

    private static void TCPSender(int port, SharedData sharedData) throws InterruptedException {
        try {
            System.out.println("TCPSender Listening on port " + port);
            while (true) {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                ArrayList<Map<String, String>> musicians = new ArrayList<Map<String, String>>();
                for (Musician musician : sharedData.sharedMusicians) {
                    if (musician.isActive()) {
                        musicians.add(musician.toMap());
                    }
                }
                System.out.println("Connection established");
                var out = new BufferedOutputStream(socket.getOutputStream());
                Gson gson = new Gson();
                out.write(gson.toJson(musicians).getBytes(StandardCharsets.UTF_8));
                out.flush();
                musicians.clear();
                socket.close();
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runActiveMusiciansUpdater(SharedData sharedData) {
        while (true) {
            for (Musician musician : sharedData.sharedMusicians) {
                long timeDiff = new Date().getTime() - musician.getLastUpdate().getTime();
                if (timeDiff > 5000) { // 5 seconds
                    musician.setActive(false);
                }
            }
        }
    }
}
