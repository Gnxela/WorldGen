package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.pipeline.pipes.*;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class GenerationPipeline {

	private final static PipeWorker[] workers = new PipeWorker[]{
			new LandmassPipeWorker(),
			new MountainPipeWorker(),
			new HeightPipeWorker(),
			new TemperaturePipeWorker(),
			new MoisturePipeWorker(),
			new PrecipitationPipeWorker(),
			new BiomePipeWorker()
	};

	private final List<Node> graphOrder = new LinkedList<>();

	public GenerationPipeline() {
		initialisePipeline();
	}

	public void setup(int seed, Sampler sampler) {
		for (PipeWorker worker : workers) {
			worker.setup(seed, sampler);
		}
	}

	public void generatePoints(Iterator<Point> pointIterator) {
		for (Node node : graphOrder) {
			System.out.println(node.producer.name());
		}
	}

	/**
	 * TODO: Improve / replace this.
	 * Dirty dependency resolution.
	 */
	private void initialisePipeline() {
		List<Node> nodes = new ArrayList<>();
		for (PipeWorker worker : workers) {
			createNodes(nodes, worker);
		}
		while (nodes.size() > 0) {
			int foundIndex = findResolvedNode(nodes);
			if (foundIndex == -1) {
				throw new RuntimeException("Unable to resolve dependency graph");
			}
			Node resolvedNode = nodes.remove(foundIndex);
			graphOrder.add(resolvedNode);
			resolveConsumers(nodes, resolvedNode);
		}
	}

	private void createNodes(List<Node> nodes, PipeWorker worker) {
		Method[] methods = worker.getClass().getDeclaredMethods();
		for (Method method : methods) {
			Producer producer = method.getAnnotation(Producer.class);
			if (producer == null) {
				continue;
			}
			// First parameter must be a point, rest must be consumers
			Parameter[] parameters = method.getParameters();
			if (!Point.class.isAssignableFrom(parameters[0].getType())) {
				throw new RuntimeException("Parameters must follow pattern [Point, Consumer, Consumer, ...]: " + worker.getClass().getSimpleName() + ":" + method.getName() + ":" + parameters[0].getName());
			}
			Consumer[] consumers = new Consumer[parameters.length-1];
			for (int i = 0; i < consumers.length; i++) {
				Consumer consumer = parameters[i+1].getAnnotation(Consumer.class);
				if (consumer == null) {
					throw new RuntimeException("Parameters must follow pattern [Point, Consumer, Consumer, ...]: " + method.getName() + ":" + parameters[i].getName());
				}
				consumers[i] = consumer;
			}
			nodes.add(new Node(method, producer, consumers));
		}
	}

	private void resolveConsumers(List<Node> nodes, Node resolvedNode) {
		for (Node node : nodes) {
			node.resolveConsumer(resolvedNode.producer);
		}
	}

	private int findResolvedNode(List<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).isAvailable()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * A node in the pipeline dependency graph.
	 */
	private static class Node {

		private final Method method;
		private final Producer producer;
		private final Consumer[] consumers;
		private final Set<String> unresolvedConsumers;

		public Node(Method method, Producer producer, Consumer[] consumers) {
			this.method = method;
			this.producer = producer;
			this.consumers = consumers;
			this.unresolvedConsumers = new HashSet<>();
			Arrays.stream(consumers).map(Consumer::name).forEach(unresolvedConsumers::add);
		}

		public void resolveConsumer(Producer producer) {
			unresolvedConsumers.remove(producer.name());
		}

		public boolean isAvailable() {
			return unresolvedConsumers.size() == 0;
		}
	}
}
