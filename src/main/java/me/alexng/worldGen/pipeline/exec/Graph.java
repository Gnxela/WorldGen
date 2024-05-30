package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.Point;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * A class that represents a pipelines DAG.
 */
public class Graph {

	private List<Node> origins = new LinkedList<>();

	/**
	 * TODO: Improve / replace this.
	 * Dirty dependency resolution.
	 * @param workers
	 */
	public void createGraph(PipeWorker[] workers) {
		List<Node> nodes = new ArrayList<>();
		for (PipeWorker worker : workers) { // O(n)
			createNodes(nodes, worker); // O(1) for a given worker
		}
		linkNodes(nodes); // O(2n)
		HashMap<Node, HashSet<String>> unresolvedConsumes = new HashMap<>();
		for (Node node : nodes) {
			HashSet<String> consumes = new HashSet<>();
			for (Consume consume : node.consumes) {
				consumes.add(consume.name());
			}
			unresolvedConsumes.put(node, consumes);
		}
		while (nodes.size() > 0) { // O(n)
			int foundIndex = findResolvedNode(nodes, unresolvedConsumes); // O(n)
			if (foundIndex == -1) {
				throw new RuntimeException("Unable to resolve dependency graph");
			}
			Node resolvedNode = nodes.remove(foundIndex);
			if (resolvedNode.consumes.length == 0) {
				origins.add(resolvedNode);
			}
			resolveConsumers(nodes, resolvedNode, unresolvedConsumes); // O(n)
		}
		// TODO: We need to validate the graph here. Ensure it is a DAG, no duplicate names, remove not-generation leaves, etc.
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
			Consume[] consumer = new Consume[parameters.length - 1];
			for (int i = 0; i < consumer.length; i++) {
				Consume c = parameters[i + 1].getAnnotation(Consume.class);
				if (c == null) {
					throw new RuntimeException("Parameters must follow pattern [Point, Consumer, Consumer, ...]: " + method.getName() + ":" + parameters[i].getName());
				}
				consumer[i] = c;
			}
			nodes.add(new Node(worker, method, producer, consumer));
		}
	}

	private void linkNodes(List<Node> nodes) {
		// TODO: These should be a better way to resolve these.
		Map<String, Node> nodeMap = new HashMap<>();
		Map<String, List<Node>> dependencyMap = new HashMap<>();
		nodes.forEach(node -> {
			nodeMap.put(node.producer.name(), node);
			Arrays.stream(node.consumes).forEach(consumer -> {
				List<Node> dependants = dependencyMap.computeIfAbsent(consumer.name(), (key) -> new LinkedList<>());
				dependants.add(node);
			});
		});
		nodes.forEach(node -> node.setAllDependencies(nodeMap));
		nodes.forEach(node -> node.setDependants(dependencyMap.computeIfAbsent(node.producer.name(), (key) -> new LinkedList<>())));
	}

	private void resolveConsumers(List<Node> nodes, Node resolvedNode, HashMap<Node, HashSet<String>> unresolvedConsumes) {
		for (Node node : nodes) {
			unresolvedConsumes.get(node).remove(resolvedNode.producer.name());
		}
	}

	private int findResolvedNode(List<Node> nodes, HashMap<Node, HashSet<String>> unresolvedConsumes) {
		for (int i = 0; i < nodes.size(); i++) {
			if (unresolvedConsumes.get(nodes.get(i)).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	public List<Node> getOrigins() {
		return origins;
	}

}
