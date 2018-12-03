package com.sensing.core.utils;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * http://www.importnew.com/30073.html
 * 此处代码有些许修改，注释掉taskTemp里的for循环，
 */
public class LatchTest {

    public static void main1(String[] args) throws InterruptedException {
        Runnable taskTemp = new Runnable() {

            // 注意，此处是非线程安全的，留坑
//            private volatile int iCounter;

            @Override
            public void run() {
//                for (int i = 0; i < 4; i++) {
                // 发起请求
//                  HttpClientOp.doGet("https://www.baidu.com/");
//                    iCounter++;
//                    System.out.println(System.nanoTime() + " [" + Thread.currentThread().getName() + "] iCounter = " + iCounter);
                System.out.println(System.nanoTime() + " [ id:" + Thread.currentThread().getId() + "  --name:" + Thread.currentThread().getName() + "] i = ");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                }
            }
        };

        LatchTest latchTest = new LatchTest();
        latchTest.startTaskAllInOnce(5, taskTemp);
    }

    public long startTaskAllInOnce(int threadNums, final Runnable task) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadNums);
        for (int i = 0; i < threadNums; i++) {
            Thread t = new Thread() {
                public void run() {
                    System.out.println(System.nanoTime() + " [-id:" + Thread.currentThread().getId() + "  --name:" + Thread.currentThread().getName() + "]");
                    try {
                        // 使线程在此等待，当开始门打开时，一起涌入门中
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            // 将结束门减1，减到0时，就可以开启结束门了
                            endGate.countDown();
                        }
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            };
            t.start();
        }
        long startTime = System.nanoTime();
        System.out.println(startTime + " [ id:" + Thread.currentThread().getId() + "   --name:" + Thread.currentThread().getName() + "] All thread is ready, concurrent going...");
        // 因开启门只需一个开关，所以立马就开启开始门
        startGate.countDown();
        // 等等结束门开启
        endGate.await();
        long endTime = System.nanoTime();
        System.out.println(endTime + " [ id:" + Thread.currentThread().getId() + "   --name:" + Thread.currentThread().getName() + "] All thread is completed.");
        return endTime - startTime;
    }


    /**
     * CountDownLatch 的简单实用
     * 要取两个快递，快递A和快递B，有两个人分别去取这两个快递
     */

    public static class getPackageATask implements Runnable {

        private CountDownLatch countDownLatch;

        public getPackageATask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                System.out.println("取快递A,话费时间3s钟");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        }
    }

    public static class getPackageBTask implements Runnable {

        private CountDownLatch countDownLatch;

        public getPackageBTask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                System.out.println("取快递B,话费时间5s钟");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        }
    }

    public static void main(String[] args) {

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//
//        thread.run();
        final CountDownLatch startGate = new CountDownLatch(1);


        long now = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(new getPackageATask(countDownLatch));
        executor.execute(new getPackageBTask(countDownLatch));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("两个快递都取到了，用时：" + (System.currentTimeMillis() - now));
    }


}