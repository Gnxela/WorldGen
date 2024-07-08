package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.Pipeline;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NaivePipelineExecutor implements PipelineExecutor{

	private Pipeline generationPipeline;

	public NaivePipelineExecutor(Pipeline generationPipeline) {
		this.generationPipeline = generationPipeline;
	}

	@Override
	public Map<String, float[]> execute(Sampler sampler) {
		Set<Node> visitedNodes = new HashSet<>();
		Map<String, float[]> resultMap = new HashMap<>();
		Map<String, float[]> finalResultMap = new HashMap<>();
		Queue<Node> nodeQueue = new LinkedList<>();
		nodeQueue.addAll(generationPipeline.getGraph().getOrigins());
		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.remove();
			float[] result = null;
			for (int i = 0; i < node.producer.iterations(); i++) {
				result = runNode(node, sampler, resultMap);
				resultMap.put(node.producer.name(), result);
			}
			if (node.producer.stored()) {
				finalResultMap.put(node.producer.name(), result);
			}
			visitedNodes.add(node);
			node.dependants.stream().filter(n -> n.dependencies.stream().allMatch(visitedNodes::contains)).forEach(n -> {
				nodeQueue.add(n);
			});
		}
		return finalResultMap;
	}

	private float[] runNode(Node node, Sampler sampler, Map<String, float[]> resultMap) {
		System.out.println(node.producer.name());
		Iterator<Point> pointIterator = sampler.getPoints();
		float[] result = new float[sampler.getSize()];
		Object[] parameters = new Object[node.consumes.length + 1];
		while (pointIterator.hasNext()) {
			Point point = pointIterator.next();
			parameters[0] = point;
			int readIndex = 0;
			for (int i = 1; i < parameters.length; i++) {
				// TODO: We shouldn't read from the map for every point. But this works for now.
				Consume consumer = node.consumes[readIndex++];
				if (!resultMap.containsKey(consumer.name())) {
					System.out.println("Failed to read consumer " + consumer.name());
				}
				if (consumer.blocked()) {
					parameters[i] = resultMap.get(consumer.name());
				} else {
					parameters[i] = resultMap.get(consumer.name())[point.getIndex()];
				}
			}

			try {
				result[point.getIndex()] = (float) node.method.invoke(node.worker, parameters);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
