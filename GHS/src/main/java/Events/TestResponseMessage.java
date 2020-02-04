package Events;

import se.sics.kompics.KompicsEvent;

public class TestResponseMessage implements KompicsEvent {
    public int type;
    public String src;
    public String dst;
    public TestResponseMessage(int type, String src, String dst) {
        this.type = type;
        this.src = src;
        this.dst = dst;
    }
}
