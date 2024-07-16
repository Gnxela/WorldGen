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
            @Nullable @Consume(name = "height", blocked = true) Float[] heightArray,
            @Consume(name = "pressure", blocked = true) Velocity[] pressureArray,
            @Consume(name = "coriolis") Velocity coriolis) {
        // TODO: Temperature map needs to be processed into pressure vector
        Velocity wind;
        int x = point.getIndex() % sampleWidth;
        int y = point.getIndex() / sampleWidth;
        Velocity pressure = pressureArray[point.getIndex()];
        if (iteration_index == 0) {
            wind = new Velocity(0.5f, pressure.direction);
        } else {
            wind = windArray[point.getIndex()];
        }
        Vector2f outputDirection = new Vector2f(wind.direction);
        if (iteration_index > 0) {
            float height = NoiseHelper.normalize(Math.max(0, heightArray[point.getIndex()]));
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int lx = x + i;
                    int ly = y + j;
                    if (lx < 0 || lx >= sampleWidth || ly < 0 || ly >= sampleHeight) {
                        continue;
                    }
                    int index = ly * sampleWidth + lx;
                    Velocity w = windArray[index];
                    int originDirection = (int) (Main.shiftAngle(Main.vectorToAngle(new Vector2f(i, j)),
                            -1 / 16f + 0.5f)
                            / 8f);
                    int windDirection = (int) (Main.shiftAngle(Main.vectorToAngle(w.direction), -1 / 16f) / 8f);
                    if (originDirection == windDirection) {
                        float heightScalar = (NoiseHelper.normalize(Math.max(0, heightArray[index])) - height) * 2;
                        float pressureStrength = 1f;//heightScalar; // + w.magnitude
                        outputDirection.x += w.direction.x * pressureStrength;
                        outputDirection.y += w.direction.y * pressureStrength;
                    }
                }
            }
        }
        float pressureStrength = 1f * pressure.magnitude;
        outputDirection.x += pressure.direction.x * pressureStrength;
        outputDirection.y += pressure.direction.y * pressureStrength;
        float coriolisStrength = 1f; //0.5f * coriolis.magnitude;
        outputDirection.x += coriolis.direction.x * coriolisStrength;
        outputDirection.y += coriolis.direction.y * coriolisStrength;
        // Vector2f v = wind.direction.normalize(new Vector2f()).mul(1 -
        // coriolisStrength)
        // .add(coriolis.direction.normalize(new
        // Vector2f()).mul(coriolisStrength)).normalize();
        return new Velocity(wind.magnitude, outputDirection.normalize());
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
        return new Velocity(Math.abs(lTemp - NoiseHelper.normalize(avg_temperature[point.getIndex()]) * 3f),
                total.div(num).normalize());
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
