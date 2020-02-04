package Events;

import se.sics.kompics.KompicsEvent;

public class ReportMessage implements KompicsEvent {
    public int w;
    public String src;
    public String dst;

    public ReportMessage(int w, String src, String dst) {
        this.w = w;
        this.src = src;
        this.dst = dst;
    }
}
