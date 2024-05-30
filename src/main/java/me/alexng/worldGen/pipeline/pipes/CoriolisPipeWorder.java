package me.alexng.worldGen.pipeline.pipes;

import org.joml.Vector2f;
import org.joml.Vector3f;

import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.sampler.PlanePoint;
import me.alexng.worldGen.sampler.PlaneSampler;
import me.alexng.worldGen.sampler.Point;
import me.alexng.worldGen.sampler.Sampler;

public class CoriolisPipeWorder implements PipeWorker {

    private static final int NUM_BANDS = 6;
    private int totalHeight = 0;
    private int bandWidth = 0;

    @Override
    public void setup(int seed, Sampler sampler) {
        totalHeight = getTotalHeight(sampler);
        bandWidth = totalHeight / NUM_BANDS;
    }

    @Producer(name = "coriolis", stored = true)
    public float process(Point point) {
        int y = getY(point);
        float d = y % bandWidth / ((float) bandWidth);
        Vector2f in = new Vector2f(0.0f, 1.0f).mul(d);
        Vector2f out = new Vector2f(1.0f, 0.0f).mul(1 - d);
        in.add(out).normalize();
        return (float) Math.tanh(in.y / in.x);
        // return ((float) Math.sin(getY(point) % bandWidth / ((float) bandWidth) * Math.PI * 2));
    }

    private int getTotalHeight(Sampler sampler) {
        if (sampler instanceof PlaneSampler) {
            return ((PlaneSampler) sampler).getTotalHeight();
        }
        throw new RuntimeException("Unknown sampler type");
    }

    private int getY(Point point) {
        if (point instanceof PlanePoint) {
            return ((PlanePoint) point).getY();
        }
        throw new RuntimeException("Unknown point type");
    }
}
