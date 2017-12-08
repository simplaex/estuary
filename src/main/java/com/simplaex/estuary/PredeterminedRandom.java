package com.simplaex.estuary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PredeterminedRandom extends Random {

  private AtomicInteger current = new AtomicInteger(0);

  private final double[] values;

  public static PredeterminedRandom of(final double... values) {
    if (!DoubleStream.of(values).allMatch(v -> v >= 0.0 && v <= 1.0)) {
      throw new IllegalArgumentException("All values must be in the range [ 0.0 .. 1.0 ]");
    }
    return new PredeterminedRandom(values);
  }

  @Override
  public synchronized void setSeed(long seed) {
    // ignored
  }

  private int nextIndex() {
    return current.getAndAccumulate(1, (current, addition) -> (current + addition) % values.length);
  }

  @Override
  protected int next(final int bits) {
    return (int) Double.doubleToLongBits(values[nextIndex()]);
  }

  @Override
  public void nextBytes(byte[] bytes) {
    for (int i = 0; i < bytes.length; i += 1) {
      bytes[i] = (byte) nextInt();
    }
  }

  @Override
  public int nextInt() {
    return (int) nextLong();
  }

  @Override
  public int nextInt(int bound) {
    return Math.abs((int) Double.doubleToLongBits(values[nextIndex()])) % bound;
  }

  @Override
  public long nextLong() {
    return Double.doubleToLongBits(nextDouble());
  }

  @Override
  public boolean nextBoolean() {
    return nextLong() % 2 == 0;
  }

  @Override
  public float nextFloat() {
    return (float) nextDouble();
  }

  @Override
  public double nextDouble() {
    return values[nextIndex()];
  }

  @Override
  public synchronized double nextGaussian() {
    return values[nextIndex()];
  }

  @Override
  public IntStream ints(long streamSize) {
    return ints().limit(streamSize);
  }

  @Override
  public IntStream ints() {
    return IntStream.generate(this::nextInt);
  }

  @Override
  public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
    return ints(streamSize);
  }

  @Override
  public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
    return ints();
  }

  @Override
  public LongStream longs(long streamSize) {
    return longs().limit(streamSize);
  }

  @Override
  public LongStream longs() {
    return LongStream.generate(this::nextLong);
  }

  @Override
  public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
    return longs().limit(streamSize);
  }

  @Override
  public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
    return longs();
  }

  @Override
  public DoubleStream doubles(long streamSize) {
    return doubles().limit(streamSize);
  }

  @Override
  public DoubleStream doubles() {
    return DoubleStream.generate(this::nextDouble);
  }

  @Override
  public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
    return doubles().limit(streamSize);
  }

  @Override
  public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
    return DoubleStream.generate(this::nextDouble);
  }
}
