package org.reactive.top1000;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName(" Top1000 should ... ")
class Top1000Test {
    public static final List<Integer> EXPECTED_SEQUENCE_ARRAYS = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
    public static final List<Integer> EXPECTED_SEQUENCE_ARRAYS_WITH_OVERFLOW = Arrays.asList(11,10,9,8,7,6,5,4,3,2);
    public static final List<Integer> EXPECTED_SEQUENCE_ARRAYS_MULTI_THREAD_ON_INCOME_BOTH = Arrays.asList(0,1,2,3,4,5,6,7,8,111);
    public static final List<Integer> EXPECTED_SEQUENCE_ARRAYS_MULTI_THREAD_ON_COME_AND_GET_TOP = Arrays.asList(109,108,107,106,105,104,103,102,101,100);
    public static final int EXPECTED_MAX_COUNT_IN_TOP = 10;
    public static final int N_THREADS = 3;
    private Top1000<Integer> top1000;
    @BeforeEach
    void setUp() {
        top1000 = new Top1000<Integer>();
    }

    @DisplayName(" on Event should remove data when inCome increses array above 6 (for testing)")
    @Test
    void onEvent() {
        for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP; i++)
            top1000.onEvent(i);

        assertEquals(EXPECTED_SEQUENCE_ARRAYS, top1000.getTop());
        assertEquals(EXPECTED_MAX_COUNT_IN_TOP, top1000.getTop().size());
    }

    @DisplayName(" on Event should remove data when inCome increses array above 1000")
    @Test
    void onEvent_throwsExceptionWhenInComeIsNull() {
        assertThrows(IllegalArgumentException.class, ()->top1000.onEvent(null));
    }

    @DisplayName(" get Top when Array has above max limit (for testing is 10)")
    @Test
    void getTop() {
        for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP; i++)
            top1000.onEvent(i);

        assertEquals(EXPECTED_SEQUENCE_ARRAYS, top1000.getTop());
    }

    @DisplayName(" getTop is waiting when Array has less max count ")
    @Test
    void getTop_whenArrayHasLessThenMaxCountInTop() throws InterruptedException {
        for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP-1; i++)
            top1000.onEvent(i);

        Thread.sleep(3000);

        top1000.onEvent(9);
        assertEquals(EXPECTED_SEQUENCE_ARRAYS, top1000.getTop());
    }

    @DisplayName(" getTop is clearing data above max count in top ")
    @Test
    void getTop_clearDataWhenArrayHasAboveData() throws InterruptedException {
        for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP-1; i++)
            top1000.onEvent(i);

        Thread.sleep(3000);
        top1000.onEvent(9);
        top1000.onEvent(10);
        top1000.onEvent(11);
        assertEquals(EXPECTED_SEQUENCE_ARRAYS_WITH_OVERFLOW, top1000.getTop());
    }

    @DisplayName(" getTop is waiting when Array has less max count ")
    @Test
    void getTop_whenArrayHasLessThenMaxCountInTopInTwoThreads() throws InterruptedException {
        for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP-1; i++)
            top1000.onEvent(i);

        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        for (int i = 0; i < N_THREADS; i++) {
            executor.submit(()->top1000.onEvent(111));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Thread.sleep(3000);

        assertEquals(EXPECTED_SEQUENCE_ARRAYS_MULTI_THREAD_ON_INCOME_BOTH, top1000.getTop());
        assertEquals(EXPECTED_MAX_COUNT_IN_TOP, top1000.getTop().size());
    }

    @DisplayName(" onEvent calls in T1, getTop calls in T2 ")
    @Test
    void getTop_TwoThreadsCallDifferentsMethods() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

        executor.submit(() -> {
            for (int i = 0; i < EXPECTED_MAX_COUNT_IN_TOP + 100; i++)
                top1000.onEvent(i);
        });

        executor.submit(()->top1000.getTop());


        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Thread.sleep(3000);
        top1000.onEvent(9);
        assertEquals(EXPECTED_SEQUENCE_ARRAYS_MULTI_THREAD_ON_COME_AND_GET_TOP, top1000.getTop());
        assertEquals(EXPECTED_MAX_COUNT_IN_TOP, top1000.getTop().size());
    }
}