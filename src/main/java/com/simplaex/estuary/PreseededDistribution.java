package com.simplaex.estuary;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A probability distribution with an internal random generator which might be pre-seeded.
 */
@FunctionalInterface
public interface PreseededDistribution {

  double draw();

  static PreseededDistribution of(final Distribution distribution) {
    return () -> distribution.draw(ThreadLocalRandom.current());
  }

  static PreseededDistribution of(final Distribution distribution, final long seed) {
    return of(distribution, new Random(seed));
  }

  static PreseededDistribution of(final Distribution distribution, final Random random) {
    return () -> distribution.draw(random);
  }

}
