import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments expected");
        }
        int UDPPort = 9904;
        int TCPPort = 2205;
        InetAddress group = InetAddress.getByName("224.0.0.1");
        SharedData sharedData = new SharedData();
        
        // Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        Runnable UDPListener = () -> {runUDPListener(UDPPort, group, sharedData);};

        Runnable TCPListener = () -> {try {
            TCPListener(TCPPort, sharedData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }};

        Thread.startVirtualThread(UDPListener);
        Thread.startVirtualThread(TCPListener);
        try {
            Thread.sleep(Duration.ofSeconds(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void runUDPListener(int port, InetAddress group, SharedData sharedData) {
        try {
            System.out.println("UDPListner Listening on port " + port);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
            while (true) {
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
                        }
                    }
                }
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

    private static void TCPListener(int port, SharedData sharedData) throws InterruptedException {
        try {
            System.out.println("TCPListener Listening on port " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                ArrayList<Map<String, String>> musicians = new ArrayList<Map<String, String>>();
                for (Musician musician : sharedData.sharedMusicians) {
                    if (musician.isActive()) {
                        musicians.add(musician.toMap());
                    }
                }
                Socket socket = serverSocket.accept();
                System.out.println("Connection established");
                var out = new BufferedOutputStream(socket.getOutputStream());
                Gson gson = new Gson();
                out.write(gson.toJson(musicians).getBytes(StandardCharsets.UTF_8));
                out.flush();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
