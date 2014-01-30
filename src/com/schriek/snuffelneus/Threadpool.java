package com.schriek.snuffelneus;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class creates pools of background threads for downloading Picasa images
 * from the web, based on URLs retrieved from Picasa's featured images RSS feed.
 * The class is implemented as a singleton; the only way to get an PhotoManager
 * instance is to call {@link #getInstance}.
 * <p>
 * The class sets the pool size and cache size based on the particular operation
 * it's performing. The algorithm doesn't apply to all situations, so if you
 * re-use the code to implement a pool of threads for your own app, you will
 * have to come up with your choices for pool size, cache size, and so forth. In
 * many cases, you'll have to set some numbers arbitrarily and then measure the
 * impact on performance.
 * <p>
 * This class actually uses two threadpools in order to limit the number of
 * simultaneous image decoding threads to the number of available processor
 * cores.
 * <p>
 * Finally, this class defines a handler that communicates back to the UI thread
 * to change the bitmap to reflect the state.
 */
@SuppressWarnings("unused")
public class Threadpool {
	// Sets the amount of time an idle thread will wait for a task before
	// terminating
	private static final int KEEP_ALIVE_TIME = 1;

	// Sets the Time Unit to seconds
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

	// Sets the initial threadpool size to 8
	private static final int CORE_POOL_SIZE = 8;

	// Sets the maximum threadpool size to 8
	private static final int MAXIMUM_POOL_SIZE = 8;

	/**
	 * NOTE: This is the number of total available cores. On current versions of
	 * Android, with devices that use plug-and-play cores, this will return less
	 * than the total number of cores. The total number of cores is not
	 * available in current Android implementations.
	 */
	private static int NUMBER_OF_CORES = Runtime.getRuntime()
			.availableProcessors();

	// A queue of Runnables for the image decoding pool
	private final BlockingQueue<Runnable> mWorkQueue;

	// A managed pool of background download threads
	private final ThreadPoolExecutor mThreadPool;

	// An object that manages Messages in a Thread
	private Handler mHandler;

	// A single instance of PhotoManager, used to implement the singleton
	// pattern
	private static Threadpool sInstance = null;

	// A static block that sets class fields
	static {

		// The time unit for "keep alive" is in seconds
		KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

		// Creates a single static instance of PhotoManager
		sInstance = new Threadpool();
	}

	/**
	 * Constructs the work queues and thread pools used to download and decode
	 * images.
	 */
	private Threadpool() {

		/*
		 * Creates a work queue for the pool of Thread objects used for
		 * downloading, using a linked list queue that blocks when the queue is
		 * empty.
		 */
		mWorkQueue = new LinkedBlockingQueue<Runnable>();

		/*
		 * Creates a new pool of Thread objects for the decoding work queue
		 */
		mThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
				KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mWorkQueue);

		/** Handler for UI updates **/
		mHandler = new Handler(Looper.getMainLooper()) {

			/*
			 * handleMessage() defines the operations to perform when the
			 * Handler receives a new Message to process.
			 */
			@Override
			public void handleMessage(Message inputMessage) {

			}
		};
	}

	/**
	 * Returns the PhotoManager object
	 * 
	 * @return The global PhotoManager object
	 */
	public static Threadpool getInstance() {

		return sInstance;
	}

	/**
	 * Cancels all Threads in the ThreadPool
	 */
	public static void cancelAll() {

		/*
		 * Creates an array of tasks that's the same size as the task work queue
		 */
		Runnable[] taskArray = new Runnable[sInstance.mWorkQueue.size()];

		// Populates the array with the task objects in the queue
		sInstance.mWorkQueue.toArray(taskArray);

		// Stores the array length in order to iterate over the array
		int taskArraylen = taskArray.length;

		/*
		 * Locks on the singleton to ensure that other processes aren't mutating
		 * Threads, then iterates over the array of tasks and interrupts the
		 * task's current Thread.
		 */
		synchronized (sInstance) {

			// Iterates over the array of tasks
			for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

				// Gets the task's current thread
				Thread thread = (Thread) taskArray[taskArrayIndex];

				// if the Thread exists, post an interrupt to it
				if (null != thread) {
					thread.interrupt();
				}
			}
		}
	}

	public static void executeRunable(Runnable r) {
		if (r == null) {
			ListLogger.Error("Could not execute task");
			return;
		}
		try {
			sInstance.mThreadPool.execute(r);

		} catch (RejectedExecutionException e) {
			ListLogger.Error(e.getMessage());
		}
	}
}
