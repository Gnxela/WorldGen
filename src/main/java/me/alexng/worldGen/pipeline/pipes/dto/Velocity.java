package me.alexng.worldGen.pipeline.pipes.dto;

import org.joml.Vector2f;

public class Velocity {
    
    public float magnitude;
    public Vector2f direction;

    public Velocity(float magnitude, Vector2f direction) {
        this.direction = direction;
        this.magnitude = magnitude;
    }
}
