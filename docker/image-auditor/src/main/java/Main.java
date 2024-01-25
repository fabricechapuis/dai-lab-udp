import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            throw new IllegalArgumentException("No arguments expected");
        }
        int port = 9904;
        InetAddress group = InetAddress.getByName("224.0.0.1");
        
        // Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        Runnable UDPListener = () -> {
            // ...

                        try {
                            System.out.println("Listening on port " + port);
                            MulticastSocket socket = new MulticastSocket(port);
                            socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
                            System.out.println("Connection established");
                            while (true) {
                                byte[] buffer = new byte[1024];
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                socket.receive(packet);
                                String message = new String(packet.getData(), 0, packet.getLength());
                                System.out.println("Received message: " + message);
                            }
                        } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread.startVirtualThread(UDPListener);
        try {
            Thread.sleep(Duration.ofSeconds(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
