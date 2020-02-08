package Events;

import se.sics.kompics.KompicsEvent;

public class RepeatMessage implements KompicsEvent {
    public String src;
    public String dst;
    public KompicsEvent event;
    public RepeatMessage(KompicsEvent event, String src, String dst){
        this.src = src;
        this.dst = dst;
        this.event = event;
    }
}
