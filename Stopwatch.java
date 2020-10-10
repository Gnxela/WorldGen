package me.alexng.untitled.generate;

public class Stopwatch {

	private long elapsedTime = 0;
	private long startTime = 0;

	public void start() {
		startTime = System.nanoTime();
	}

	public long stop() {
		long localElapsedTime = System.nanoTime() - startTime;
		elapsedTime += localElapsedTime;
		return localElapsedTime;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public float getElapsedSeconds() {
		return elapsedTime / 1000000000f;
	}
}
