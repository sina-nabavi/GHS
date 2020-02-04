package Ports;

import Events.*;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(InitiateMessage.class);
    positive(ReportMessage.class);
    negative(InitiateMessage.class);
    negative(ReportMessage.class);
    positive(ChangeCoreMessage.class);
    negative(ChangeCoreMessage.class);
    positive(TestMessage.class);
    negative(TestMessage.class);
    positive(TestResponseMessage.class);
    negative(TestResponseMessage.class);
    positive(ConnectMessage.class);
    negative(ConnectMessage.class);
    negative(RepeatMessage.class);
    positive(RepeatMessage.class);
}}
