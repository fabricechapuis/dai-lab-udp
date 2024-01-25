import java.net.*;
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

        try {
            InetAddress group = InetAddress.getByName("224.0.0.1");
            int port = 9904;

            while (true) {
                String json = createJson();
                byte[] buffer = json.getBytes();

                System.out.println("Sending " + json);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                DatagramSocket socket = new DatagramSocket();
                System.out.println("packet sent");
                socket.send(packet);

                Thread.sleep(1000);
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
        jsonMap.put("instrument", instrument);
        jsonMap.put("sound", sound);
        jsonMap.put("uuid", uuid);

        Gson gson = new Gson();
        return gson.toJson(jsonMap);
    }
}
