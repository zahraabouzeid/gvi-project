package com.gvi.project.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutHelper {
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void setTimeout (Runnable task, long delay, TimeUnit unit) {

		if (scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(1);
		}

		scheduler.schedule( () -> {
			try {
				task.run();
			} finally {
				scheduler.shutdown();
			}
		}, delay, unit);
	}

	public static void setTimeout (Runnable task, long delayMillis) {

		if (scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(1);
		}

		scheduler.schedule( () -> {
			try {
				task.run();
			} finally {
				scheduler.shutdown();
			}
		}, delayMillis, TimeUnit.MILLISECONDS);
	}
}
