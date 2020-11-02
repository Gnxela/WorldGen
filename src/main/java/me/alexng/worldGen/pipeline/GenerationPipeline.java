package me.alexng.worldGen.pipeline;

import me.alexng.worldGen.pipeline.pipes.*;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

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

	private final List<Node> graphOrder = new ArrayList<>();

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
			String dependencies = Arrays.stream(node.consumers).map(Consumer::name).collect(Collectors.joining(", "));
			String dependants = node.dependants.stream().map(n -> n.producer.name()).collect(Collectors.joining(", "));
			System.out.println("[" + dependencies + "] -> " + node.producer.name() + " -> [" + dependants + "]");
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
		linkNodes(nodes);
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
			Consumer[] consumers = new Consumer[parameters.length - 1];
			for (int i = 0; i < consumers.length; i++) {
				Consumer consumer = parameters[i + 1].getAnnotation(Consumer.class);
				if (consumer == null) {
					throw new RuntimeException("Parameters must follow pattern [Point, Consumer, Consumer, ...]: " + method.getName() + ":" + parameters[i].getName());
				}
				consumers[i] = consumer;
			}
			nodes.add(new Node(method, producer, consumers));
		}
	}

	private void linkNodes(List<Node> nodes) {
		Map<String, List<Node>> dependencyMap = new HashMap<>();
		nodes.forEach(node -> Arrays.stream(node.consumers).forEach(consumer -> {
			List<Node> dependants = dependencyMap.computeIfAbsent(consumer.name(), (key) -> new LinkedList<>());
			dependants.add(node);
		}));
		nodes.forEach(node -> node.setDependants(dependencyMap.computeIfAbsent(node.producer.name(), (key) -> new LinkedList<>())));
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
		// TODO: This is only needed during pipeline initialisation
		private final Set<String> unresolvedConsumers;
		private List<Node> dependants;

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

		public void setDependants(List<Node> dependants) {
			this.dependants = dependants;
		}
	}
}
