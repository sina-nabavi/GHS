package Components;

import Events.*;
import Ports.EdgePort;
import misc.TableRow;
import se.sics.kompics.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node extends ComponentDefinition {

    public static ArrayList<String> mstEdges = new ArrayList<String>();
    //SE params
    private static final int BASE = 0;
    private static final int BRANCH = 1;
    private static final int REJECTED = -1;

    //SN params
    private static final int FOUND = 0;
    private static final int FIND = 1;

    //Test Response params
    private static final int Accept = 0;
    private static final int Reject = -1;



    Positive<EdgePort> recievePort = positive(EdgePort.class);
    Negative<EdgePort> sendPort = negative(EdgePort.class);
    HashMap<String,Integer> edgeStates = new HashMap<String,Integer>();
    String nodeName;
    String fragName;
    int level = 0;
    int findCount = 0;
    int nodeState = 0;
    int bestWt ;
    String bestEdge;
    String inBranch;
    String testEdge;
    HashMap<String,Integer> neighbours = new HashMap<>();
    ArrayList<TableRow> route_table = new ArrayList<>();

    void writeMSTEdgeOnFile(ConnectMessage event){
        String edge = event.src;
        edge += '-';
        edge += nodeName;
        edge += ',';
        edge += neighbours.get(event.src).toString();
        String edgeDuplicate = nodeName;
        edgeDuplicate += '-';
        edgeDuplicate += event.src;
        edgeDuplicate += ',';
        edgeDuplicate += neighbours.get(event.src).toString();
        if(Node.mstEdges.contains(edgeDuplicate) == false) {
            mstEdges.add(edge);
            Writer output;
            try {
                String line = edge + "\n";
                output = new BufferedWriter(new FileWriter("src/main/java/" + "MST" + ".txt", true));
                output.append(line);

                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void report(){
        if(findCount == 0 && testEdge == null){
            nodeState = FOUND;
            trigger(new ReportMessage(bestWt, nodeName, inBranch),sendPort);
        }
    }

    void changeCore(){
        if(edgeStates.get(bestEdge) == BRANCH){
            trigger(new ChangeCoreMessage(nodeName, bestEdge),sendPort);
        }
        else{
            trigger(new ConnectMessage(level, nodeName, bestEdge),sendPort);
            edgeStates.put(bestEdge, BRANCH);
        }
    }

    void test(){
        testEdge = null;
        int minW = 10000;
        for( Map.Entry<String, Integer> entry : neighbours.entrySet()){
            if(edgeStates.get(entry.getKey()) != BASE)
                continue;
            if(entry.getValue() < minW){
                minW = entry.getValue();
                testEdge = entry.getKey();
            }
        }
        if(testEdge == null)
            report();
        else
            trigger(new TestMessage(level, fragName, nodeName, testEdge),sendPort);
    }

    Handler testHandler = new Handler<TestMessage>(){
        @Override
        public void handle(TestMessage event){
            if(nodeName.equalsIgnoreCase(event.dst)){
                System.out.println("TEST MESSAGE" + " " + event.src + " " + event.dst + " " + event.F + " " + event.L);
                if(event.F != fragName && event.L <= level)
                    trigger(new TestResponseMessage(Accept, event.dst, event.src),sendPort);
                if(event.L > level)
                    trigger(new RepeatMessage(event, event.dst, event.src),sendPort);
                else {
                    if(edgeStates.get(event.src) == BASE){
                        edgeStates.put(event.src, REJECTED);
                        if(testEdge != event.src)
                            trigger(new TestResponseMessage(Reject, event.dst, event.src),sendPort);
                        else
                            test();
                    }
                }
            }
        }
    };

    Handler initiateHandler = new Handler<InitiateMessage>(){
        @Override
        public void handle(InitiateMessage event){
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("INITIATE MESSAGE" + " " + event.src + " " + event.dst + " " + event.S + " " + event.F + " " + event.L);
                level = event.L;
                fragName = event.F;
                nodeState = event.S;
                System.out.println("inBranch" + nodeName + " " + event.src);
                inBranch = event.src;
                bestEdge = null;
                bestWt = 10000;
                for (Map.Entry<String, Integer> entry : neighbours.entrySet()) {
                    if (edgeStates.get(entry.getKey()) != BRANCH || entry.getKey() == event.src)
                        continue;
                    trigger(new InitiateMessage(event.L, event.F, event.S, nodeName, entry.getKey()), sendPort);
                    if (event.S == FIND)
                        findCount += 1;
                }
                if (event.S == FIND)
                    test();
            }
        }
    };

    Handler testResponseHandler = new Handler<TestResponseMessage>(){
        @Override
        public void handle(TestResponseMessage event){
            if(nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("TEST RESPONSE MESSAGE" + " " + event.src + " " + event.dst + " " + event.type);
                if (event.type == Accept) {
                    testEdge = null;
                    if (neighbours.get(event.src) < bestWt) {
                        bestEdge = event.src;
                        bestWt = neighbours.get(event.src);
                    }
                    report();
                } else if (event.type == Reject) {
                    if (edgeStates.get(event.src) == BASE) {
                        edgeStates.put(event.src, REJECTED);
                        test();
                    }
                }
            }
        }
    };

    Handler reportHandler = new Handler<ReportMessage>(){
        @Override
        public void handle(ReportMessage event){
            if(nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("REPORT MESSAGE" + " " + event.src + " " + event.dst + " " + event.w);
                if (!event.src.equals(inBranch)) {
                    findCount -= 1;
                    if (event.w < bestWt) {
                        bestEdge = event.src;
                        bestWt = neighbours.get(event.src);
                    }
                    report();
                }
                else if (nodeState == FIND && edgeStates.get(event.src) == BASE){
                    trigger(new RepeatMessage(event, event.dst, event.src),sendPort);
                }
                else{
                    if (event.w > bestWt) {
                        changeCore();
                    }
                    if(event.w <= bestWt) {
                        trigger(new ReportMessage(event.w, event.dst ,event.src),sendPort);
                    }
                    else if (neighbours.get(event.src) == bestWt && bestWt == 10000)
                        System.out.println("failed to find MST");

                }
            }
        }
    };

    Handler changeCoreHandler = new Handler<ChangeCoreMessage>(){
        @Override
        public void handle(ChangeCoreMessage event){
            if(nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("CHANGE CORE MESSAGE" + " " + event.src + " " + event.dst);
                changeCore();
            }
        }

    };

    Handler connectHandler = new Handler<ConnectMessage>(){
        @Override
        public void handle(ConnectMessage event) {
            if (nodeName.equalsIgnoreCase(event.dst)) {
                System.out.println("CONNECT MESSAGE" + " " + event.src + " " + event.dst + " " + event.level);
                if (event.level < level) {
                    edgeStates.put(event.src, BRANCH);
                    writeMSTEdgeOnFile(event);
                    trigger(new InitiateMessage(level, fragName, nodeState, event.dst, event.src), sendPort);
                    if (nodeState == FIND) {
                        findCount += 1;
                    }
                }
                else if(edgeStates.get(event.src) == BASE)
                    trigger(new RepeatMessage(event, event.dst, event.src),sendPort);
                else{
                    writeMSTEdgeOnFile(event);
                    trigger(new InitiateMessage(level + 1, neighbours.get(event.src).toString(), FIND, event.dst, event.src), sendPort);
                }
            }
        }
    };

    Handler repeatHandler = new Handler<RepeatMessage>(){
        @Override
        public void handle(RepeatMessage event){
            if(nodeName.equalsIgnoreCase(event.dst)){
                System.out.println("REPEAT MESSAGE" + " " + event.src + " " + event.dst + " " + event.event.getClass().toString());
                trigger(event.event, sendPort);
            }
        }
    };
    Handler startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            int minAdj = 10000;
            String m = "";
            for (Map.Entry<String, Integer> entry : neighbours.entrySet()) {
                edgeStates.put(entry.getKey(), BASE);
                if (entry.getValue() < minAdj){
                    m = entry.getKey();
                    minAdj = entry.getValue();
                }
            }
            edgeStates.put(m, BRANCH);
            level = 0;
            nodeState = FOUND;
            findCount = 0;
            fragName = nodeName;
            trigger(new ConnectMessage(0,nodeName, m),sendPort);
        }
    };

    public Node(InitMessage initMessage) {
        nodeName = initMessage.nodeName;
        System.out.println("initNode :" + initMessage.nodeName);
        this.neighbours = initMessage.neighbours;
        subscribe(startHandler, control);
        subscribe(connectHandler, recievePort);
        subscribe(initiateHandler, recievePort);
        subscribe(testHandler, recievePort);
        subscribe(reportHandler,recievePort);
        subscribe(testResponseHandler, recievePort);
        subscribe(changeCoreHandler, recievePort);
        subscribe(repeatHandler, recievePort);

    }


}
