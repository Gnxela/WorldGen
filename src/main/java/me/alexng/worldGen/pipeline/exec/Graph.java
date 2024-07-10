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
	 * 
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
				if (node.producer.iterated() && node.producer.name().equals(consume.name())) {
					continue;
				}
				consumes.add(consume.name());
			}
			unresolvedConsumes.put(node, consumes);
		}
		while (nodes.size() > 0) { // O(n)
			int foundIndex = findResolvedNode(nodes, unresolvedConsumes); // O(n)
			if (foundIndex == -1) {
				for (Node n : unresolvedConsumes.keySet()) {
					if (!unresolvedConsumes.get(n).isEmpty()) {
						System.out.println(n.producer.name());
					}
				}
				throw new RuntimeException("Unable to resolve dependency graph");
			}
			Node resolvedNode = nodes.remove(foundIndex);
			if (resolvedNode.consumes.length == 0) {
				origins.add(resolvedNode);
				System.out.println("Origin:");
			}
			resolveConsumers(nodes, resolvedNode, unresolvedConsumes); // O(n)
		}
		// TODO: We need to validate the graph here. Ensure it is a DAG, no duplicate
		// names, remove not-generation leaves, etc.
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
				throw new RuntimeException(
						"Parameters must follow pattern [Point, [Integer], Consumer, Consumer, ...]: "
								+ worker.getClass().getSimpleName() + ":" + method.getName() + ":"
								+ parameters[0].getName());
			}
			int readIndex = 1;
			if (producer.iterated()) {
				readIndex++;
				// ????
				if (!parameters[1].getType().toString().equals("int")) {
					throw new RuntimeException(
							"Parameters must follow pattern [Point, [Integer], Consumer, Consumer, ...]: "
									+ worker.getClass().getSimpleName() + ":" + method.getName() + ":"
									+ parameters[1].getName());
				}
			}
			Consume[] consumer = new Consume[parameters.length - readIndex];
			for (int writeIndex = 0; readIndex < parameters.length; writeIndex++) {
				// TODO: This does nothing really. check type instead
				// if (c == null) {
				// throw new RuntimeException(
				// "Parameters must follow pattern [Point, [Integer], Consumer, Consumer, ...]:
				// "
				// + method.getName() + ":" + parameters[--readIndex].getName());
				// }
				consumer[writeIndex] = parameters[readIndex++].getAnnotation(Consume.class);
			}
			nodes.add(new Node(worker, method, producer, consumer));
		}
	}

	private void linkNodes(List<Node> nodes) {
		// TODO: These should be a better way to resolve these.
		Map<String, Node> nodeMap = new HashMap<>();
		Map<String, List<Node>> dependencyMap = new HashMap<>();
		for (Node node : nodes) {
			nodeMap.put(node.producer.name(), node);
			for (Consume consumer : node.consumes) {
				if (node.producer.name().equals(consumer.name())) {
					continue;
				}
				List<Node> dependants = dependencyMap.computeIfAbsent(consumer.name(), (key) -> new LinkedList<>());
				dependants.add(node);
			}
		}
		nodes.forEach(node -> node.setAllDependencies(nodeMap));
		nodes.forEach(node -> node
				.setDependants(dependencyMap.computeIfAbsent(node.producer.name(), (key) -> new LinkedList<>())));
	}

	private void resolveConsumers(List<Node> nodes, Node resolvedNode,
			HashMap<Node, HashSet<String>> unresolvedConsumes) {
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
