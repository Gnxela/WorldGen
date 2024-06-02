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
        return (temperature + coriolis) / 2.0f;
    }

    @Producer(name = "temp_average", stored = true)
    public float tempAverage(PlanePoint point, @Consume(name = "temperature", blocked = true) float[] temperature) {
        Vector2f val = new Vector2f();
        int num = 0;
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
                float t = temperature[ny * sampleWidth + nx];
                val.add(new Vector2f(nx - lx == 0 ? 0 : t / (float) (nx - lx),
                        ny - ly == 0 ? 0 : t / (float) (ny - ly)));
                num++;
            }
        }
        return (float) Math.tanh((val.y / val.x));
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
