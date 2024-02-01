import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedData {
    public volatile ConcurrentLinkedQueue<Musician> sharedMusicians = new ConcurrentLinkedQueue<Musician>();
}
