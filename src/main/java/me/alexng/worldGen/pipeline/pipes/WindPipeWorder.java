package me.alexng.worldGen.pipeline.pipes;

import org.joml.Vector2f;

import me.alexng.worldGen.Nullable;
import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.pipeline.pipes.dto.Velocity;
import me.alexng.worldGen.sampler.PlanePoint;
import me.alexng.worldGen.sampler.PlaneSampler;
import me.alexng.worldGen.sampler.Sampler;

public class WindPipeWorder implements PipeWorker {

    private static final int SAMPLE_WIDTH = 30;
    private int sampleWidth, sampleHeight;

    @Override
    public void setup(int seed, Sampler sampler) {
        sampleWidth = getSampleWidth(sampler);
        sampleHeight = getSampleHeight(sampler);
    }

    @Producer(name = "wind", stored = true, iterated = true, iterations = 10)
    public Velocity process(
            PlanePoint point,
            int iteration_index,
            @Nullable @Consume(name = "wind", blocked = true) Velocity[] windArray,
            @Consume(name = "temp_average") Float temperature,
            @Consume(name = "coriolis") Velocity coriolis) {
        // TODO: Convert angles and then add.
        // TODO: Need to support magnitudes in angles. Maybe allow for Object[] as map values?
        // TODO: Temperature map needs to be processed into pressure vector
        Velocity wind;
        if (iteration_index == 0) {
            wind = new Velocity(0, new Vector2f());
        } else {
            wind = windArray[point.getIndex()];
        }
        float coriolisStrength = 0.02f;
        wind.direction.x += Math.cos(temperature) * 0.1;
        wind.direction.y -= Math.sin(temperature) * 0.1;
        Vector2f v = wind.direction.normalize().mul(1 - coriolisStrength, new Vector2f()).add(coriolis.direction.normalize().mul(coriolisStrength, new Vector2f()));
        return new Velocity(0.5f, v);
    }

    @Producer(name = "temp_average", stored = true)
    public Float tempAverage(PlanePoint point, @Consume(name = "temperature", blocked = true) Float[] temperature) {
        int num = 0;
        float total = 0;
        int lx = point.getIndex() % sampleWidth;
        int ly = point.getIndex() / sampleWidth;
        for (int dx = 0; dx < SAMPLE_WIDTH; dx++) {
            int nx = lx + dx - SAMPLE_WIDTH / 2;
            if (nx < 0 || nx >= sampleWidth) {
                continue;
            }
            for (int dy = 0; dy < SAMPLE_WIDTH; dy++) {
                int ny = ly + dy - SAMPLE_WIDTH / 2;
                if (ny < 0 || ny >= sampleHeight) {
                    continue;
                }
                total += temperature[ny * sampleWidth + nx];
                num++;
            }
        }
        return total / num;
    }

    private int getSampleHeight(Sampler sampler) {
        if (sampler instanceof PlaneSampler) {
            return ((PlaneSampler) sampler).getNumPointsY();
        }
        throw new RuntimeException("Unknown sampler type");
    }

    private int getSampleWidth(Sampler sampler) {
        if (sampler instanceof PlaneSampler) {
            return ((PlaneSampler) sampler).getNumPointsX();
        }
        throw new RuntimeException("Unknown sampler type");
    }
}
