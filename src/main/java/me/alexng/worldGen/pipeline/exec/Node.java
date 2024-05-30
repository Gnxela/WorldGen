package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A node in the pipeline dependency graph.
 */
class Node {

	// The worker this node is running on.
	final PipeWorker worker;
	final Method method;
	// The value that this node produces
	final Producer producer;
	// The values the node consumes
	final Consume[] consumes;
	boolean storedOrBlockingDependants;
	// Nodes that this node depends on.
	final List<Node> dependencies = new LinkedList<>();
	// Nodes that depend on this node.
	List<Node> dependants;
	ConcurrentHashMap<String, Payload> valueMap = new ConcurrentHashMap<>();

	public Node(PipeWorker worker, Method method, Producer producer, Consume[] consumes) {
		this.worker = worker;
		this.method = method;
		this.producer = producer;
		this.consumes = consumes;
		this.storedOrBlockingDependants = producer.stored();
	}

	public void setDependants(List<Node> dependants) {
		storedOrBlockingDependants = storedOrBlockingDependants | dependants.stream()
				.anyMatch(dependant -> Arrays.stream(dependant.consumes).anyMatch(Consume::blocked));
		this.dependants = dependants;
	}

	public void setAllDependencies(Map<String, Node> nodeMap) {
		for (Consume consume : consumes) {
			dependencies.add(nodeMap.get(consume.name()));
		}
	}

	public float[] receive(String name, int batchNumber) {
		return valueMap.get(name + "." + batchNumber).payload;
	}

	public void deliver(String name, int batchNumber, float[] output) {
		valueMap.put(name + "." + batchNumber, new Payload(output));
	}
}
