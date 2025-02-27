package org.pitest.mutationtest.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pitest.classinfo.ClassName;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.engine.MutationDetails;

/**
 * Assigns tests based on line coverage and order them by execution speed with a
 * weighting towards tests whose names imply they are intended to test the
 * mutated class
 *
 * @author henry
 *
 */
public class DefaultTestPrioritiser implements TestPrioritiser {

  private static final int       TIME_WEIGHTING_FOR_DIRECT_UNIT_TESTS = 1000;

  private final CoverageDatabase coverage;

  public DefaultTestPrioritiser(CoverageDatabase coverage) {
    this.coverage = coverage;
  }

  @Override
  public List<TestInfo> assignTests(MutationDetails mutation) {
    return prioritizeTests(mutation.getClassName(), pickTests(mutation));
  }

  private Collection<TestInfo> pickTests(MutationDetails mutation) {
    return this.coverage.getTestsForClassLine(mutation.getClassLine());
  }

  private List<TestInfo> prioritizeTests(ClassName clazz,
      Collection<TestInfo> testsForMutant) {
    final List<TestInfo> sortedTis = new ArrayList<>(testsForMutant);
    sortedTis.sort(new TestInfoPriorisationComparator(clazz, TIME_WEIGHTING_FOR_DIRECT_UNIT_TESTS));
    return sortedTis;
  }

}
