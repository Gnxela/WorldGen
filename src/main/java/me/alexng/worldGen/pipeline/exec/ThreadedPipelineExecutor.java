package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Pipeline;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadedPipelineExecutor implements PipelineExecutor {

	// TODO: We want to be able to return exceptions instead of just printing errors
	// and returning.

	private static final int POINT_BATCH_SIZE = 100;

	private final Pipeline pipeline;
	private final int numThreads;
	private final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();
	private final AtomicBoolean isCompleted = new AtomicBoolean(false);
	private final ConcurrentHashMap<Node, LinkedList<Payload>> storage = new ConcurrentHashMap<>();

	public ThreadedPipelineExecutor(int numThreads, Pipeline pipeline) {
		this.pipeline = pipeline;
		this.numThreads = numThreads;
	}

	@Override
	public Map<String, float[]> execute(Sampler sampler) {
		Thread[] threadPool = new Thread[numThreads];
		for (int i = 0; i < threadPool.length; i++) {
			threadPool[i] = new Thread(new WorkerThread(pipeline, isCompleted, jobQueue, storage));
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
		Point[] points = new Point[POINT_BATCH_SIZE];
		int index = 0;
		while (pointIterator.hasNext()) {
			if (index < POINT_BATCH_SIZE) {
				points[index++] = pointIterator.next();
			} else {
				index = 0;
				for (Node origin : pipeline.getGraph().getOrigins()) {
					if (jobId == 0) {
						System.out.println("Origin " + origin.producer.name());
					}
					jobQueue.put(new Job(jobId++, jobId, points, origin, new Object[0]));
				}
				points = new Point[POINT_BATCH_SIZE];
			}
		}
	}

	private static class WorkerThread implements Runnable {

		private final Pipeline pipeline;
		private final AtomicBoolean isCompleted;
		private final BlockingQueue<Job> jobQueue;
		private final ConcurrentHashMap<Node, LinkedList<Payload>> storage;

		public WorkerThread(Pipeline pipeline, AtomicBoolean isCompleted, BlockingQueue<Job> jobQueue,
				ConcurrentHashMap<Node, LinkedList<Payload>> storage) {
			this.pipeline = pipeline;
			this.isCompleted = isCompleted;
			this.jobQueue = jobQueue;
			this.storage = storage;
		}

		@Override
		public void run() {
			try {
				while (!isCompleted.get()) {
					Job job = jobQueue.take();
					System.out.println("Running: " + job.node.producer.name());
					float[] output = runJob(job);
					storage.computeIfAbsent(job.node, (n) -> new LinkedList<>()).add(new Payload(output));
				}
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}

		private float[] runJob(Job job) {
			Object[] parameters = new Object[job.node.consumes.length + 1];
			float[] output = new float[job.points.length];
			for (int i = 0; i < job.points.length; i++) {
				parameters[0] = job.points[i];
				try {
					System.arraycopy(job.inputData, 0, parameters, 1, job.inputData.length);
				} catch (Exception e) {
					System.out.println("Error: " + job.node.producer.name());
				throw e;
			}

				try {
					output[i] = (float) job.node.method.invoke(job.node.worker, parameters);
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
		private final Object[] inputData;

		public Job(int id, int batchNumber, Point[] points, Node node, Object[] inputData) {
			this.id = id;
			this.batchNumber = batchNumber;
			this.points = points;
			this.node = node;
			this.inputData = inputData;
		}
	}
}
