package com.simplaex.estuary;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Distributions {

  public static Distribution beta(final double a, final double b) {
    return BetaDistribution.get(a, b);
  }

}
