package Events;

import se.sics.kompics.KompicsEvent;

public class TestMessage implements KompicsEvent {
    public int L;
    public String F;
    public String src;
    public String dst;

    public TestMessage(int L, String F, String src, String dst) {
        this.L = L;
        this.F = F;
        this.src = src;
        this.dst = dst;
    }
}
