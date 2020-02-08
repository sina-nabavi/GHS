package Events;

import se.sics.kompics.KompicsEvent;

public class ConnectMessage implements KompicsEvent {
    public int level;
    public String src;
    public String dst;
    public ConnectMessage(int level, String src, String dst) {
        this.level = level;
        this.src = src;
        this.dst = dst;
    }
}
