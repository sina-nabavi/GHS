package Events;

import Components.Node;
import se.sics.kompics.Init;

import java.util.ArrayList;
import java.util.HashMap;

public class InitMessage extends Init<Node> {
    public String nodeName;
    public HashMap<String,Integer> neighbours = new HashMap<>();

    public InitMessage(String nodeName,
                       HashMap<String,Integer> neighbours) {
        this.nodeName = nodeName;
        this.neighbours = neighbours;
    }
}