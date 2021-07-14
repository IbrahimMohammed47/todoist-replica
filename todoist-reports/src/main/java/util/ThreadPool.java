package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

	private static ExecutorService threadPool;
	private static int THREAD_COUNT;
	final private static int MIN_THREAD_COUNT = 1;
	final private static int MAX_THREAD_COUNT = 40;
	private static boolean isFirstTime = true;

	// Singleton Design Pattern
	public static ExecutorService getThreadPool() {
		if (isFirstTime) {
			Dotenv dotenv = Dotenv.load();
			THREAD_COUNT = Integer.parseInt(Optional.ofNullable(dotenv.get("MAX_THREAD_COUNT")).orElse("20"));
			isFirstTime = false;
		}

		if (threadPool == null) {
			threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		}

		return threadPool;
	}

	public static void stopAllThreads() throws Exception {
		if (!threadPool.isShutdown()) {
			threadPool.shutdown(); // Disable new tasks from being submitted

			// Wait a minute for existing tasks to terminate
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow(); // Cancel currently executing tasks
				// Wait another minute for tasks to respond to being cancelled
				if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
					throw new Exception("app thread pool did not freeze");
				}
			}

		}else {
			throw new Exception("app thread pool already freezed");
		}
	}

	public static void startAllThreads() throws Exception {
		if (threadPool.isShutdown()) {
			threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
			return;
		}
		throw new Exception("app thread pool should be freezed first");
	}

	public static void changeNumOfThreads(int n) throws Exception {
		if (threadPool.isShutdown()) {
			if (n >= 1 && n <= 40) {
				THREAD_COUNT = n;
				return;
			}
			throw new Exception("thread count should be between " + MIN_THREAD_COUNT + " and " + MAX_THREAD_COUNT);
		}
		throw new Exception("app thread pool should be freezed first");
	}
}