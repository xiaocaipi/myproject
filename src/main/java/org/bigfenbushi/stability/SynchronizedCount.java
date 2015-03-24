package org.bigfenbushi.stability;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SynchronizedCount {

	public int count = 0;
	
	static class Job implements Runnable{
		private SynchronizedCount count;
		private CountDownLatch countDown;
		public Job(SynchronizedCount count,CountDownLatch countDown){
			this.count = count;
			this.countDown = countDown;
		}
		@Override
		public void run() {
			synchronized (count) {
				count.count++;
			}
			countDown.countDown();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDown = new CountDownLatch(1000);
		SynchronizedCount count = new SynchronizedCount();
		ExecutorService ex = Executors.newFixedThreadPool(5);
		for(int i=0;i<1500;i++){
			ex.execute(new Job(count, countDown));
		}
		countDown.await();
		System.out.println(count.count);
		ex.shutdown();
	}

}
