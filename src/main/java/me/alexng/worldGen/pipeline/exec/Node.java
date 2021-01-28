package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Consumer;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A node in the pipeline dependency graph.
 */
class Node {

	// The worker on which this node is running.
	final PipeWorker worker;
	final Method method;
	// The value that this node produces
	final Producer producer;
	// The values the node consumes
	final Consumer[] consumers;
	// TODO: This is only needed during pipeline initialisation. Move to an local map<Node, String> instance
	final Set<String> unresolvedConsumers;
	boolean storedOrBlockingDependants;
	// Nodes that this node depends on.
	final List<Node> dependencies = new LinkedList<>();
	// Nodes that depend on this node.
	List<Node> dependants;

	public Node(PipeWorker worker, Method method, Producer producer, Consumer[] consumers) {
		this.worker = worker;
		this.method = method;
		this.producer = producer;
		this.consumers = consumers;
		this.unresolvedConsumers = new HashSet<>();
		this.storedOrBlockingDependants = producer.stored();
		Arrays.stream(consumers).map(Consumer::name).forEach(unresolvedConsumers::add);
	}

	public void resolveConsumer(Producer producer) {
		unresolvedConsumers.remove(producer.name());
	}

	public boolean isAvailable() {
		return unresolvedConsumers.size() == 0;
	}

	public void setDependants(List<Node> dependants) {
		storedOrBlockingDependants = storedOrBlockingDependants | dependants.stream()
				.anyMatch(dependant -> Arrays.stream(dependant.consumers).anyMatch(Consumer::blocked));
		this.dependants = dependants;
	}

	public void grabDependencies(Map<String, Node> nodeMap) {
		for (Consumer consumer : consumers) {
			dependencies.add(nodeMap.get(consumer.name()));
		}
	}

	public void deliver(String name, int batchNumber, Object[] output) {

	}
}
