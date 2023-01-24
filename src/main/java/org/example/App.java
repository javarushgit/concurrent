package org.example;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */
public class App 
{
    static List<Integer> l;
    public static void main( String[] args ) throws InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(4,
            () -> System.out.println("Some work before threads wake up"));
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
             executorService.execute(new Task(cyclicBarrier));
        }

    }


    static class PhaserSimilarToCyrcleBarier implements Runnable {
        Phaser phaser;

        public PhaserSimilarToCyrcleBarier(Phaser phaser) {
            this.phaser = phaser;
        }

        @Override
        public void run() {
            System.out.println("Some work");
            phaser.arriveAndAwaitAdvance();
            System.out.println("Next work");
        }
    }


    static class PhaserSimilarToCountDownLatch implements Runnable {
        Phaser phaser;

        public PhaserSimilarToCountDownLatch(Phaser phaser) {
            this.phaser = phaser;
        }

        @Override
        public void run() {
            System.out.println("Do some work");
            phaser.arrive();
        }
    }

    static class Task implements Runnable {
        CyclicBarrier cyclicBarrier;

        public Task(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + "Some work");
                int x = cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() + " is: " + x);
                //System.out.println("Some work");
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }


    static class Worker implements Runnable {
        CountDownLatch countDownLatch;

        public Worker(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            System.out.println(Thread.currentThread().getName() + " done his work");
            countDownLatch.countDown();
        }
    }



    static class Restaurant {
        String name = "MyRest";
    }


    static class Person implements Runnable{
        Semaphore tables;
        Restaurant restaurant;
        public Person(Semaphore tables, Restaurant restaurant) {
            this.tables = tables;
            this.restaurant = restaurant;
        }

        @Override
        public void run() {
            System.out.println( Thread.currentThread().getName() + " is waiting till tables free");
            try {
                tables.acquire();
                System.out.println( Thread.currentThread().getName() + " I got restaurant name = " + restaurant.name);
                tables.release();
                System.out.println("Available permits: " + tables.availablePermits());
                System.out.println( Thread.currentThread().getName() + " I went out from rest\n--------------------------------------------------");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    static class Producer implements Runnable {
        BlockingQueue<Integer> blockingQueue;


        public Producer(BlockingQueue<Integer> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                try {
                    System.out.println("Producer " + " start ");
                    blockingQueue.put(i);
                    System.out.println("Producer " + " put " + i + "to queue with size " + blockingQueue.size());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    static class Consumer implements Runnable {
        BlockingQueue<Integer> blockingQueue;


        public Consumer(BlockingQueue blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                System.out.println("Consumer " + " start ");
                try {
                    System.out.println("Consumer" + " took " +blockingQueue.take() + " from queue with size " + blockingQueue.size());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }





    static void checkList(List<Integer> list) throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Long> t1 = executorService.submit(new ListRunner(list, latch, 0, 1000));
        Future<Long> t2 = executorService.submit(new ListRunner(list, latch, 1000, 2000));

        latch.countDown();
        System.out.println("Thread 1: " + t1.get());
        System.out.println("Thread 2: " + t2.get());
    }
    static void fillList(List<Integer> list) {
        for (int i = 0; i < 2000; i++) {
            list.add(i);
        }
    }

    static class ListRunner implements Callable<Long> {
        List<Integer> list;
        CountDownLatch latch;
        int first;
        int last;

        public ListRunner(List<Integer> list, CountDownLatch latch, int first, int last) {
            this.list = list;
            this.latch = latch;
            this.first = first;
            this.last = last;
        }

        @Override
        public Long call() throws Exception {
            latch.await();
            long start = System.nanoTime();
            for (int i = first; i < last; i++) {
                list.get(i);
            }

            long end = System.nanoTime();

            return (end-start) / 1000;
        }

    }
}
