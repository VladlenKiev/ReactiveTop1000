package org.reactive.top1000;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class Client {

    private String clientName;

    private String versionGenerate;
    private String connectedDate;

    public void connectToGenerator() {
        Observable<HashMap<String, String>> generatorInfo = Generator.getGeneratorInfo();
        Disposable subscribe = generatorInfo.subscribe(this::connect);
        System.out.println("subscribe = " + subscribe);


    }

    @SneakyThrows
    public void subscribeToEmitterFromGenerator() {

//        PublishSubject<String> subject = PublishSubject.create();
        Observable<String> obs = Generator.magicPublisher();
//        obs.subscribe(System.out::println);
//        final Observable<String> ob = Generator.magicPublisher();
//        System.out.println("First subscribed");
//        ob.subscribe(System.out::println);
        Thread.sleep(3000);
        obs.subscribe(System.out::println);

        System.out.println("subscribe = " + obs);
        System.in.read();



    }

    private void connect(HashMap<String, String> subscriberInfo) {

        this.versionGenerate = subscriberInfo.get("version");
        this.connectedDate = subscriberInfo.get("date");
    }

}
