package com.simplaex.estuary;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "ConstantConditions"})
@RunWith(Spectrum.class)
public class BetaDistributionTest {
  {
    describe("beta distributions", () -> {

      it("should emit a beta distribution α = 2, β = 5", () -> {
        val dist = BetaDistribution.get(2.0, 5.0).generate(ThreadLocalRandom.current(), 100000, 20);
        val max = dist.values().stream().max(Double::compare).get();
        expect(dist.get(0.2) / (double) max).toEqual(1.0);
      });

      it("should emit a beta distribution α = 0.5, β = 0.5", () -> {
        val dist = BetaDistribution.get(0.5, 0.5).generate(ThreadLocalRandom.current(), 100000, 20);
        val res = dist.values().stream().sorted(Comparator.reverseOrder()).limit(2).collect(Collectors.toList());
        expect(dist.values().stream().allMatch(v -> v <= res.get(0) || v <= res.get(1))).toBeTrue();
      });

      it("should emit a beta distribution α = 0.2, β = 0.2", () -> {
        val dist = BetaDistribution.get(0.2, 0.2).generate(ThreadLocalRandom.current(), 100000, 20);
        val res = dist.values().stream().sorted(Comparator.reverseOrder()).limit(2).collect(Collectors.toList());
        expect(dist.values().stream().allMatch(v -> v <= res.get(0) || v <= res.get(1))).toBeTrue();
      });

      it("should emit a beta distribution α = 0.2, β = 3", () -> {
        val dist = BetaDistribution.get(0.2, 3).generate(ThreadLocalRandom.current(), 100000, 20);
        val max = dist.values().stream().max(Long::compare).get();
        val min = dist.values().stream().min(Long::compare).get();
        val firstKey = dist.keySet().stream().min(Double::compare).get();
        val lastKey = dist.keySet().stream().max(Double::compare).get();
        expect(dist.get(firstKey)).toEqual(max);
        expect(dist.get(lastKey)).toEqual(min);
      });

    });

  }
}
