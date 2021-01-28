package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Consumer;
import me.alexng.worldGen.pipeline.Pipeline;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadedPipelineExecutor implements PipelineExecutor {

	// TODO: We want to be able to return exceptions instead of just printing errors and returning.

	private static final int JOB_SIZE = 100;

	private final Pipeline pipeline;
	private final int numThreads;
	private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();
	private final AtomicBoolean isCompleted = new AtomicBoolean(false);

	public ThreadedPipelineExecutor(int numThreads, Pipeline pipeline) {
		this.pipeline = pipeline;
		this.numThreads = numThreads;
	}

	@Override
	public Map<String, float[]> execute(Sampler sampler) {
		Thread[] threadPool = new Thread[numThreads];
		for (int i = 0; i < threadPool.length; i++) {
			threadPool[i] = new Thread(new WorkerThread(isCompleted, jobQueue));
			threadPool[i].start();
		}

		try {
			createInitialJobs(sampler);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("Initial job count: " + jobQueue.size());
		// TODO: Actually made this work and return a result
		return null;
	}

	private void createInitialJobs(Sampler sampler) throws InterruptedException {
		int jobId = 0;
		Iterator<Point> pointIterator = sampler.getPoints();
		Point[] points = new Point[JOB_SIZE];
		int index = 0;
		while (pointIterator.hasNext()) {
			if (index < JOB_SIZE) {
				points[index++] = pointIterator.next();
			} else {
				index = 0;
				for (Node origin : pipeline.getGraph().getOrigins()) {
					jobQueue.put(new Job(jobId++, jobId, points, origin));
				}
				points = new Point[JOB_SIZE];
			}
		}
	}

	private static class WorkerThread implements Runnable {

		private final AtomicBoolean isCompleted;
		private final BlockingQueue<Job> jobQueue;

		public WorkerThread(AtomicBoolean isCompleted, BlockingQueue<Job> jobQueue) {
			this.isCompleted = isCompleted;
			this.jobQueue = jobQueue;
		}

		@Override
		public void run() {
			try {
				while (!isCompleted.get()) {
					Job job = jobQueue.take();
					Object[] output = runJob(job);
					if (output == null) {
						System.err.println("Job failed to execute");
						return;
					}

					for (Node dependant : job.node.dependants) {
						dependant.deliver(job.node.producer.name(), job.batchNumber, output);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private Object[] runJob(Job job) {
			Object[][] inputData = new Object[job.node.consumers.length][];
			for (int i = 0; i < job.node.consumers.length; i++) {
				Consumer consumer = job.node.consumers[i];
				// TODO: If we no longer need this data in memory based on the graph. Remove it
				// inputData[i] = storage.get(job.id, consumer.name());
			}
			Object[] parameters = new Object[job.node.consumers.length + 1];
			Object[] output = new Object[job.points.length];
			for (int i = 0; i < job.points.length; i++) {
				parameters[0] = job.points[i];
				System.arraycopy(inputData[i], 0, parameters, 1, inputData[i].length);

				try {
					output[i] = job.node.method.invoke(job.node.worker, parameters);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			}
			return output;
		}
	}

	/**
	 * A class that represents a unit of work for a node.
	 * Computes the nodes output for a given set of points.
	 */
	private static class Job {
		// The jobs unique Id
		private final int id;
		// The points batch this job is running on
		private final int batchNumber;
		private final Point[] points;
		private final Node node;

		public Job(int id, int batchNumber, Point[] points, Node node) {
			this.id = id;
			this.batchNumber = batchNumber;
			this.points = points;
			this.node = node;
		}
	}
}
