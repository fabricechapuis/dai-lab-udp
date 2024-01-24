import java.util.HashMap;
import java.util.Map;

class Main {
    static String instrument;
    static String sound;
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
}