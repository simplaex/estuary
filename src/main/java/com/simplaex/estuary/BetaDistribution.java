package com.simplaex.estuary;

import lombok.experimental.UtilityClass;

/**
 * An implementation of beta distributions.
 * <p>
 * Ported from https://compbio.soe.ucsc.edu/gen_sequence/gen_beta.c
 * <p>
 * References:
 * <p>
 * Generate one number from the distribution specified by gen.
 * Returns a single random deviate from the beta distribution with
 * parameters A and B.
 * <p>
 * Method
 * R. C. H. Cheng
 * Generating Beta Variates with Nonintegral Shape Parameters
 * Communications of the ACM, 21:317-322  (1978)
 * (Algorithms BB and BC)
 * <p>
 * Atkinson, A. C.
 * A family of switching algorithms for the computer generation of
 * beta random variables.
 * Biometrika 66:141-145. (1979)
 * <p>
 * Joehnk, M.D.
 * Erzeugung von Betaverteilten und Param[1]verteilten Zufallszahlen.
 * Metrika, 8:5-15. (1964)
 * <p>
 * Cited in
 * Dagpunar, John.
 * Principles of Random Variate Generation.
 * Oxford University Press, 1988.
 */
@UtilityClass
@SuppressWarnings({"NonAsciiCharacters", "WeakerAccess"})
public class BetaDistribution {

  public static Distribution get(final double α, final double β) {

    final double min = Math.min(α, β);
    final double max = Math.max(α, β);
    final double sum = α + β;

    if (max < 0.5) {
      // use Joehnk's algorithm, no precomputation
      return random -> {
        double u1, u2, logv, logw, logSum;
        do {
          u1 = random.nextDouble();
          u2 = random.nextDouble();
          logv = Math.log(u1) / α;
          logw = Math.log(u2) / β;
          logSum = logv > logw
            ? logv + Math.log(1 + Math.exp(logw - logv))
            : logw + Math.log(1 + Math.exp(logv - logw));
        } while (logSum > 0.0);
        assert logv <= logSum;
        return Math.exp(logv - logSum);
      };

    } else if (min > 1.0) {
      // use Cheng's Branch and Bound Algorithm
      final double λ = Math.sqrt((sum - 2.0) / (2.0 * α * β - sum));
      final double c = min + 1.0 / λ;
      return random -> {
        double u1, u2, v, w, z, r, s, t;
        do {
          u1 = random.nextDouble();
          u2 = random.nextDouble();
          v = λ * Math.log(u1 / (1.0 - u1));
          w = min * Math.exp(v);
          if (w == Double.POSITIVE_INFINITY) {
            w = Double.MAX_VALUE;
          }
          z = u1 * u1 * u2;
          r = c * v - 1.38629436112;
          s = min + r - w;
          if (s + 2.609438 >= 5.0 * z) {
            break;
          }
          t = Math.log(z);
        } while (r + sum * Math.log(sum / (max + w)) < t);
        return (α == min ? w : max) / (β + w);
      };

    } else {
      // use Atkinson's switching method, as described in Dagpunar's book
      final double t, r;
      if (max > 1.0) {
        t = (1 - min) / (1 + max - min);
        r = max * t / (max * t + min * Math.pow(1 - t, max));
      } else if (min == 1.0) {
        t = 0.5;
        r = 0.5;
      } else {
        t = 1.0 / (1.0 + Math.sqrt(max * (1 - max) / (min * (1 - min))));
        r = max * t / (max * t + min * (1 - t));
      }
      if (max >= 1.0) {
        return random -> {
          double u1, u2, w;
          for (; ; ) {
            u1 = random.nextDouble();
            u2 = random.nextDouble();
            if (u1 < r) {
              w = t * Math.pow(u1 / r, 1 / min);
              if (Math.log(u2) < (max - 1) * Math.log((1 - w))) {
                break;
              }
            } else {
              w = 1 - (1 - t) * Math.pow((1 - u1) / (1 - r), 1 / max);
              if (Math.log(u2) < (min - 1) * Math.log(w / t)) {
                break;
              }
            }
          }
          return (α == min) ? w : 1 - w;
        };
      } else {
        return random -> {
          double u1, u2, w;
          for (; ; ) {
            u1 = random.nextDouble();
            u2 = random.nextDouble();
            if (u1 < r) {
              w = t * Math.pow(u1 / r, 1 / min);
              if (Math.log(u2) < (max - 1) * Math.log((1 - w) / (1 - t))) {
                break;
              }
            } else {
              w = 1 - (1 - t) * Math.pow((1 - u1) / (1 - r), 1 / max);
              if (Math.log(u2) < (min - 1) * Math.log(w / t)) {
                break;
              }
            }
          }
          return (α == min) ? w : 1 - w;
        };
      }

    }
  }

}
