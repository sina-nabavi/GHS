package Events;

import se.sics.kompics.KompicsEvent;

public class ChangeCoreMessage implements KompicsEvent {
    public String src;
    public String dst;
    public ChangeCoreMessage(String src, String dst){
        this.src = src;
        this.dst = dst;
    }
}
