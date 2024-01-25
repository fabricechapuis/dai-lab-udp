import java.net.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

class Main {
    static String instrument;
    static String sound;
    static String uuid;

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("We need exactly one argument");
        }
        Map<String, String> sounds = initMap();

        if (sounds.containsKey(args[0])) {
            instrument = args[0];
            sound = sounds.get(args[0]);
            System.out.println("I am a " + instrument + " and I go " + sound);
        } else {
            throw new IllegalArgumentException("Unknown instrument");
        }

        uuid = UUID.randomUUID().toString();

        String json = createJson();
        byte[] buffer = json.getBytes();

        try {
            InetAddress group = InetAddress.getByName("224.0.0.1");
            int port = 9904;
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            DatagramSocket socket = new DatagramSocket();
            while (true) {
                System.out.println("Sending " + json);
                socket = new DatagramSocket();
                socket.send(packet);
                System.out.println("packet sent");

                Thread.sleep(Duration.ofSeconds(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> initMap() {
        Map<String, String> sounds = new HashMap<String, String>();
        sounds.put("piano", "ti-ta-ti");
        sounds.put("trumpet", "pouet");
        sounds.put("flute", "trulu");
        sounds.put("violin", "gzi-gzi");
        sounds.put("drum", "boum-boum");
        return sounds;
    }

    private static String createJson() {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("uuid", uuid);
        jsonMap.put("sound", sound);

        Gson gson = new Gson();
        return gson.toJson(jsonMap);
    }
}
