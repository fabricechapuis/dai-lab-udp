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
    final private String instrument;
    private Date lastUpdate;
    private boolean isActive = true;

    Musician(String uuid, String sound) {
        this.uuid = uuid;
        this.instrument = SOUNDS.get(sound);
        if (this.instrument == null) {
            throw new IllegalArgumentException("Unknown sound");
        }
        this.updateTimeStamp();
    }

    Date getLastUpdate() {
        return this.lastUpdate;
    }

    void updateTimeStamp() {
        this.lastUpdate = new Date();
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
        map.put("lastActivity", String.valueOf(this.lastUpdate.getTime()));
        return map;
    }
}
