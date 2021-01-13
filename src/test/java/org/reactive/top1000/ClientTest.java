package org.reactive.top1000;

import io.reactivex.Observable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName(" Client should ...")
class ClientTest {

    @DisplayName(" create client with general info ")
    @Test
    void startSimpleClientsWithGeneralInfo() {

        Client client = new Client();
        client.setClientName("FirstClient");
        client.connectToGenerator();

//        System.out.println("generatorInfo = " + generatorInfo);
    }

    @DisplayName(" subscribe to emitter ")
    @Test
    void subscribeToEmitter() {

        Client client = new Client();
        client.setClientName("FirstClient");
//        client.connectToGenerator();
        client.subscribeToEmitterFromGenerator();

//        System.out.println("generatorInfo = " + generatorInfo);
    }

}