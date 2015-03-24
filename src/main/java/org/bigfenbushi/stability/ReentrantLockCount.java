package org.bigfenbushi.stability;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockCount {

	public int count = 0;
	private final ReentrantLock lock = new ReentrantLock();
	
	static class Job implements Runnable{
		private ReentrantLockCount count;
		private CountDownLatch countDown;
		public Job(ReentrantLockCount count,CountDownLatch countDown){
			this.count = count;
			this.countDown = countDown;
		}
		@Override
		public void run() {
			count.lock.lock();
			count.count++;
			count.lock.unlock();
			countDown.countDown();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDown = new CountDownLatch(1000);
		ReentrantLockCount count = new ReentrantLockCount();
		ExecutorService ex = Executors.newFixedThreadPool(5);
		for(int i=0;i<1500;i++){
			ex.execute(new Job(count, countDown));
		}
		countDown.await();
		System.out.println(count.count);
		ex.shutdown();
	}

}
