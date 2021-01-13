package org.reactive.top1000;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {

    public static Observable<HashMap<String, String>> getGeneratorInfo() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("version", "1");
        stringStringHashMap.put("author", "me");
        stringStringHashMap.put("date", LocalDateTime.now().toString());
        Observable<HashMap<String, String>> just = Observable.just(stringStringHashMap);
        System.out.println("just = " + just);
        return just;
    }

    public static Observable<String> magicPublisher() {
        Random randomDelay = new Random(1);
        AtomicInteger atomicInteger = new AtomicInteger();
        final Observable<String> obs = Observable.<String>generate(emitter ->
                emitter.onNext("" + atomicInteger.getAndAdd(1)))
                .concatMap(s -> Observable.just(s).delay(randomDelay.nextInt(1000), TimeUnit.MILLISECONDS))
                .subscribeOn(Schedulers.newThread());

        //Он сразу подписывается и обсервебл тановится горячим! Без этого он бдует холодный!

        PublishSubject<String> subject = PublishSubject.create();
        obs.subscribe(subject);

        //За счет того что этот метод одновременно и подписчик и паблишер, есть возможность в горячий обсервебл
        //докинуть сообщение!
        new Thread(() -> {
           try {
               Thread.sleep(5000);
               subject.onNext("4321");
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        }).start();

        return subject;
//        return obs;
    }
}
