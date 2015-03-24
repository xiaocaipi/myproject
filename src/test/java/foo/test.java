package foo;

import java.util.concurrent.Semaphore;

public class test {

	 static Semaphore semaphore = new Semaphore(100);
		public static void main(String[] args) throws InterruptedException {
			
			for(int i=0;i<300;i++){
				semaphore.acquire();
				System.out.println(semaphore.getQueueLength());
			}
		}
}
