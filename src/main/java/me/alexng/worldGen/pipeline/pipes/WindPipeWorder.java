package me.alexng.worldGen.pipeline.pipes;

import org.joml.Vector2f;

import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
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

    @Producer(name = "wind", stored = true)
    public float process(PlanePoint point, @Consume(name = "temp_average") float temperature,
            @Consume(name = "coriolis") float coriolis) {
        // TODO: Convert angles and then add.
        // TODO: Temperature map needs to be processed into pressure vector
        return (temperature + coriolis);
    }

    @Producer(name = "temp_average", stored = true)
    public float tempAverage(PlanePoint point, @Consume(name = "temperature", blocked = true) float[] temperature) {
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
