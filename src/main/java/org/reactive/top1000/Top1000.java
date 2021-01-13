package org.reactive.top1000;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 Service which returns top (with maximum value) 1000 elements from endless
 data source of incoming elements.

 P.S. Declarations of methods could be change accordingly without changing their names.
        */
public class Top1000<T extends Comparable<T>> {

    public static final int MAX_COUNT_IN_TOP = 10;
    private final List<T> incomeStoredList = new ArrayList<>();
    /**
     * Would be called in case of incoming element.
     * One incoming element would be passed as argument
     *
     * aserobaba@luxoft.com
     */
    //threadsafe (few calling) - case: T1 calls onEvent, T2 - getTop
    public synchronized void onEvent(T income) {
        if (income == null)
            throw new IllegalArgumentException("Cannot be null");
//        synchronized (income)
        if (incomeStoredList.isEmpty()
                || income.compareTo(Collections.max(incomeStoredList)) > 0) {
            System.out.println("income = " + income);
            incomeStoredList.add(income);
        }

        resizeToMaxCountInTop();
    }

    /**
     * Returns top (with maximum value) 1000 elements.
     * Could be called anytime.
     */
    // can return only 1000 and now less. If Less 1000 then thread is waiting
    @SneakyThrows
    public synchronized List<T> getTop() {

        while (incomeStoredList.size() < MAX_COUNT_IN_TOP) {
            System.out.println("I'm sleeping. ActualStored = " + incomeStoredList.size());
//            Thread.sleep(1000);
            wait();
        }

        resizeToMaxCountInTop();

        return incomeStoredList;
    }

    private synchronized void resizeToMaxCountInTop() {
        if (incomeStoredList.size() > MAX_COUNT_IN_TOP) {
            incomeStoredList.sort(Comparator.reverseOrder());
            System.out.println("incomeStoredList = " + incomeStoredList);
            incomeStoredList.subList(MAX_COUNT_IN_TOP, incomeStoredList.size()).clear();
        }
    }
}


