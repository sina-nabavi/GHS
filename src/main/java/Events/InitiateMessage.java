package Events;

import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    public int L;
    public String F;
    public int S;
    public String src;
    public String dst;
    public InitiateMessage(int L, String F, int S, String src, String dst) {
        this.L = L;
        this.F = F;
        this.S = S;
        this.src = src;
        this.dst = dst;
    }
}
