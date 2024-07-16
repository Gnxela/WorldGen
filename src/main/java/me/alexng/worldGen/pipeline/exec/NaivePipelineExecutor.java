package me.alexng.worldGen.pipeline.exec;

import me.alexng.worldGen.ColorMaps;
import me.alexng.worldGen.Main;
import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.Pipeline;
import me.alexng.worldGen.sampler.PlaneSampler;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NaivePipelineExecutor implements PipelineExecutor {

	private Pipeline generationPipeline;

	public NaivePipelineExecutor(Pipeline generationPipeline) {
		this.generationPipeline = generationPipeline;
	}

	@Override
	public Map<String, Object[]> execute(Sampler sampler) {
		Set<Node> visitedNodes = new HashSet<>();
		Map<String, Object[]> resultMap = new HashMap<>();
		Map<String, Object[]> finalResultMap = new HashMap<>();
		Queue<Node> nodeQueue = new LinkedList<>();
		nodeQueue.addAll(generationPipeline.getGraph().getOrigins());
		while (!nodeQueue.isEmpty()) {
			Node node = nodeQueue.remove();
			Object[] result = null;
			for (int i = 0; i < node.producer.iterations(); i++) {
				result = runNode(node, i, sampler, resultMap);
				resultMap.put(node.producer.name(), result);
				if (node.producer.name().equals("wind")) {
					int width = ((PlaneSampler) sampler).getNumPointsX();
					int height = ((PlaneSampler) sampler).getNumPointsY();
					try {
						Main.writeMapDataToPng(width, height, result, ColorMaps.VELOCITY_TO_HSL,
								"maps/" + node.producer.name() + "_" + i + ".png");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (node.producer.stored()) {
				finalResultMap.put(node.producer.name(), result);
			}
			visitedNodes.add(node);
			node.dependants.stream().filter(n -> n.dependencies.stream().allMatch(visitedNodes::contains))
					.forEach(n -> {
						nodeQueue.add(n);
					});
		}
		// return finalResultMap;
		return resultMap;
	}

	private Object[] runNode(Node node, int iteration_index, Sampler sampler, Map<String, Object[]> resultMap) {
		long start = System.currentTimeMillis();
		Iterator<Point> pointIterator = sampler.getPoints();
		Object[] result = (Object[]) Array.newInstance(node.method.getReturnType(), sampler.getSize());
		Object[] parameters = new Object[node.consumes.length + 1 + (node.producer.iterated() ? 1 : 0)];
		int writeIndex = 0;
		int readIndex = 0;
		LinkedList<UpdatableParameter> updatableParameters = new LinkedList<>();
		updatableParameters.add(new UpdatableParameter(writeIndex++, null));
		if (node.producer.iterated()) {
			parameters[writeIndex++] = iteration_index;
		}
		for (; writeIndex < parameters.length; writeIndex++) {
			// TODO: We shouldn't read from the map for every point. But this works for now.
			Consume consumer = node.consumes[readIndex++];
			Object[] r = resultMap.get(consumer.name());
			if (r == null && node.producer.iterated() && node.producer.name().equals(consumer.name())) {
				if (consumer.blocked()) {
					parameters[writeIndex] = (Object[]) Array.newInstance(node.method.getReturnType(), 0);
				} else {
					updatableParameters.add(new UpdatableParameter(writeIndex, consumer));
				}
			} else {
				if (consumer.blocked()) {
					parameters[writeIndex] = r;
				} else {
					updatableParameters.add(new UpdatableParameter(writeIndex, consumer));
				}
			}
		}
		while (pointIterator.hasNext()) {
			Point point = pointIterator.next();
			for (UpdatableParameter parameter : updatableParameters) {
				if (parameter.index == 0) {
					parameters[parameter.index] = point;
					continue;
				}
				Object[] r = resultMap.get(parameter.consumer.name());
				if (r != null) {
					parameters[parameter.index] = r[point.getIndex()];
				} else {
					parameters[parameter.index] = 0f;
				}
			}
			try {
				result[point.getIndex()] = node.method.invoke(node.worker, parameters);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		System.out.println(node.producer.name() + " (" + (System.currentTimeMillis() - start) / 1000f + "s)");
		return result;
	}

	private static class UpdatableParameter{
		public int index;
		public Consume consumer;

		public UpdatableParameter(int index, Consume consumer) {
			this.index = index;
			this.consumer = consumer;
		}
	}
}
