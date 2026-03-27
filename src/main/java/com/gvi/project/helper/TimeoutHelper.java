package com.gvi.project.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutHelper {
	private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void setTimeout (Runnable task, long delay, TimeUnit unit) {
		scheduleTimeout(task, delay, unit);
	}

	public static void setTimeout (Runnable task, long delayMillis) {
		scheduleTimeout(task, delayMillis, TimeUnit.MILLISECONDS);
	}

	private static void scheduleTimeout(Runnable task, long delay, TimeUnit unit) {
		ScheduledExecutorService activeScheduler = ensureScheduler();

		activeScheduler.schedule(() -> {
			try {
				task.run();
			} finally {
				// Keep the shutdown tied to the scheduler that accepted this task.
				activeScheduler.shutdown();
			}
		}, delay, unit);
	}

	private static synchronized ScheduledExecutorService ensureScheduler() {
		if (scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
		}

		return scheduler;
	}
}
