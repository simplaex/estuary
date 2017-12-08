package com.simplaex.estuary;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * A probability distribution.
 */
@FunctionalInterface
public interface Distribution {

  double draw(final Random random);

  default PreseededDistribution preseeded() {
    return PreseededDistribution.of(this);
  }

  default PreseededDistribution preseeded(final long seed) {
    return PreseededDistribution.of(this, seed);
  }

  default PreseededDistribution preseeded(final Random random) {
    return PreseededDistribution.of(this, random);
  }

  default Map<Double, Long> generate(final Random random, final int samples, final int size) {
    final Map<Double, Long> values = new TreeMap<>();
    for (int i = 0; i < samples; i += 1) {
      final double v = draw(random);
      final double r = Math.round(v * size);
      final double k = r / size;
      final Long n = values.getOrDefault(k, 0L);
      values.put(k, n + 1);
    }
    return values;
  }

  default double[] generate(final Random random, final int size) {
    final double[] values = new double[size];
    return fill(random, values);
  }

  default double[] fill(final Random random, final double[] values) {
    for (int i = 0; i < values.length; i += 1) {
      values[i] = draw(random);
    }
    return values;
  }

}
