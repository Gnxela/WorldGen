package me.alexng.worldGen.pipeline.pipes;

import org.joml.Math;
import org.joml.Vector2f;

import me.alexng.worldGen.pipeline.PipeWorker;
import me.alexng.worldGen.pipeline.Producer;
import me.alexng.worldGen.pipeline.pipes.dto.Velocity;
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
    public Velocity process(Point point) {
        int bandIndex = getY(point) / bandWidth;
        float d = (getY(point) % bandWidth) / ((float) bandWidth);
        Vector2f direction;
        switch (bandIndex) {
            case 0:
                direction = new Vector2f(-1.0f, 0.0f).mul(d).add(new Vector2f(0.0f, 1.0f).mul(1 - d)).normalize();
                // output = new Vector2f(1.0f, 0.0f);
                break;
            case 1:
                // output = new Vector2f(-1.0f, 0.0f);
                direction = new Vector2f(1.0f, 0.0f).mul(1 - d).add(new Vector2f(0.0f, -1.0f).mul(d)).normalize();
                break;
            case 2:
                // output = new Vector2f(0.0f, 1.0f);
                direction = new Vector2f(-1.0f, 0.0f).mul(d).add(new Vector2f(0.0f, 1.0f).mul(1 - d)).normalize();
                break;
            case 3:
                // output = new Vector2f(0.0f, -1.0f);
                direction = new Vector2f(-1.0f, 0.0f).mul(1 - d).add(new Vector2f(0.0f, -1.0f).mul(d)).normalize();
                break;
            case 4:
                direction = new Vector2f(1.0f, 0.0f).mul(d).add(new Vector2f(0.0f, 1.0f).mul(1 - d)).normalize();
                break;
            case 5:
                direction = new Vector2f(-1.0f, 0.0f).mul(1 - d).add(new Vector2f(0.0f, -1.0f).mul(d)).normalize();
                break;
            default:
                throw new RuntimeException("Invalid band");
        }
        float m = (float) Math.abs(Math.sin((getY(point) / (double) totalHeight - 0.5) * Math.PI));
        return new Velocity(m, direction);
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
