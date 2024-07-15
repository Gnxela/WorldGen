package me.alexng.worldGen.pipeline.pipes;

import java.util.Random;

import org.joml.Math;
import org.joml.Vector2f;

import me.alexng.worldGen.Main;
import me.alexng.worldGen.NoiseHelper;
import me.alexng.worldGen.Nullable;
import me.alexng.worldGen.pipeline.Consume;
import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.pipeline.pipes.dto.Velocity;
import me.alexng.worldGen.sampler.PlanePoint;
import me.alexng.worldGen.sampler.PlaneSampler;
import me.alexng.worldGen.sampler.Sampler;

public class WindPipeWorder implements PipeWorker {

    private int sampleWidth, sampleHeight;
    private Random random;

    @Override
    public void setup(int seed, Sampler sampler) {
        sampleWidth = getSampleWidth(sampler);
        sampleHeight = getSampleHeight(sampler);
        random = new Random(seed);
    }

    @Producer(name = "wind", stored = true, iterated = true, iterations = 10)
    public Velocity process(
            PlanePoint point,
            int iteration_index,
            @Nullable @Consume(name = "wind", blocked = true) Velocity[] windArray,
            @Consume(name = "pressure", blocked = true) Velocity[] pressure,
            @Consume(name = "coriolis") Velocity coriolis) {
        // TODO: Temperature map needs to be processed into pressure vector
        Velocity wind;
        int x = point.getIndex() % sampleWidth;
        int y = point.getIndex() / sampleWidth;
        if (iteration_index == 0) {
            return new Velocity(0.5f, pressure[point.getIndex()].direction);
        } else {
            wind = windArray[point.getIndex()];
        }
        for (int i = -1; i < 1; i++) {
            for (int j = -1; j < 1; j++) {
                int lx = x + i;
                int ly = y + j;
                if (lx < 0 || lx > sampleWidth || ly < 0 || ly > sampleHeight) {
                    continue;
                }
                Velocity p = pressure[ly * sampleWidth + lx];
                float pressureStrength = p.magnitude;
                int originDirection = (int) (Main.shiftAngle(Main.vectorToAngle(new Vector2f(i, j)), -1 / 16f + 0.5f)
                        / 8f);
                int pressureDirection = (int) (Main.shiftAngle(Main.vectorToAngle(p.direction), -1 / 16f) / 8f);
                if (originDirection == pressureDirection) {
                    wind.direction.x += p.direction.x * pressureStrength;
                    wind.direction.y += p.direction.y * pressureStrength;
                }
            }
        }
        float coriolisStrength = 0.01f * coriolis.magnitude;
        wind.direction.x += coriolis.direction.x * coriolisStrength;
        wind.direction.y += coriolis.direction.y * coriolisStrength;
        // Vector2f v = wind.direction.normalize(new Vector2f()).mul(1 -
        // coriolisStrength)
        // .add(coriolis.direction.normalize(new
        // Vector2f()).mul(coriolisStrength)).normalize();
        return new Velocity(0.5f, wind.direction);
    }

    @Producer(name = "pressure", stored = true)
    public Velocity pressure(PlanePoint point, @Consume(name = "temperature", blocked = true) Float[] temperature,
            @Consume(name = "temp_average", blocked = true) Float[] avg_temperature) {
        final int SAMPLE_WIDTH = 5;
        Vector2f total = new Vector2f();
        Vector2f totalWeight = new Vector2f();
        int num = 0;
        float lTemp = NoiseHelper.normalize(temperature[point.getIndex()]);
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

                if (nx == lx && ny == ly) {
                    continue;
                }

                Vector2f weight = new Vector2f(lTemp - NoiseHelper.normalize(avg_temperature[ny * sampleWidth + nx]));
                total.add(new Vector2f(lx - nx, ly - ny).normalize().mul(weight));
                totalWeight.add(weight);
                num++;

                // Vector2f weight = new Vector2f(NoiseHelper.normalize(temperature[ny *
                // sampleWidth + nx] - lTemp));
                // total.add(
                // new Vector2f(1f, 1f).div(nx - lx + 1e-6f, ny - ly + 1e-6f).mul(weight));
                // totalWeight.add(weight);
            }
        }
        return new Velocity(Math.abs(lTemp - NoiseHelper.normalize(avg_temperature[point.getIndex()])) * 10f,
                total.div(num));
    }

    @Producer(name = "temp_average", stored = true)
    public Float tempAverage(PlanePoint point, @Consume(name = "temperature", blocked = true) Float[] temperature) {
        final int SAMPLE_WIDTH = 9;
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
