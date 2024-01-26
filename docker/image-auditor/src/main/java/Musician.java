import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Musician {
    private String uuid;
    final private static Map<String, String> SOUNDS = new HashMap<String, String>() {{
        put("ti-ta-ti", "piano");
        put("pouet", "trumpet");
        put("trulu", "flute");
        put("gzi-gzi", "violin");
        put("boum-boum", "drum");
    }};
    private String instrument;
    private Timestamp lastUpdate;
    private boolean isActive = true;

    Musician(String uuid, String sound) {
        this.uuid = uuid;
        this.instrument = SOUNDS.get(sound);
        if (this.instrument == null) {
            throw new IllegalArgumentException("Unknown sound");
        }
        this.updateTimeStamp();
    }

    void updateTimeStamp() {
        this.lastUpdate = new Timestamp(new Date().getTime());
    }

    boolean isActive() {
        return this.isActive;
    }

    void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    String getUuid() {
        return this.uuid;
    }

    Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("uuid", this.uuid);
        map.put("instrument", this.instrument);
        map.put("lastActivity", this.lastUpdate.toString());
        return map;
    }
}
