package org.bigfenbushi.stability;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCount {

	public AtomicInteger count = new AtomicInteger();
	
	static class Job implements Runnable{
		private AtomicCount count;
		private CountDownLatch countDown;
		public Job(AtomicCount count,CountDownLatch countDown){
			this.count = count;
			this.countDown = countDown;
		}
		@Override
		public void run() {
			boolean isSuccess = false;
			while(!isSuccess){
				int countValue = count.count.get();
				isSuccess = count.count.compareAndSet(countValue, countValue+1);
			}
			countDown.countDown();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDown = new CountDownLatch(1000);
		AtomicCount count = new AtomicCount();
		ExecutorService ex = Executors.newFixedThreadPool(5);
		for(int i=0;i<1500;i++){
			ex.execute(new Job(count, countDown));
		}
		countDown.await();
		System.out.println(count.count.get());
		ex.shutdown();
	}

}
