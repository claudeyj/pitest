/*
 * Copyright 2011 Henry Coles
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.pitest.mutationtest.build;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.MutationConfig;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MutationSource {

  private final MutationConfig       mutationConfig;
  private final TestPrioritiser      testPrioritiser;
  private final ClassByteArraySource source;
  private final MutationInterceptor interceptor;
  private final Collection<String>   failingTests;

  private static final Logger      LOG = Log.getLogger();

  public MutationSource(final MutationConfig mutationConfig,
      final TestPrioritiser testPrioritiser,
      final ClassByteArraySource source,
      final MutationInterceptor interceptor) {
    this(mutationConfig, testPrioritiser, source, interceptor, new ArrayList<String>());
  }

  public MutationSource(final MutationConfig mutationConfig,
      final TestPrioritiser testPrioritiser,
      final ClassByteArraySource source,
      final MutationInterceptor interceptor,
      Collection<String> failingTests) {
    this.mutationConfig = mutationConfig;
    this.testPrioritiser = testPrioritiser;
    this.source = source;
    this.interceptor = interceptor;
    this.failingTests = failingTests;
  }

  public Collection<MutationDetails> createMutations(final ClassName clazz) {

    final Mutater m = this.mutationConfig.createMutator(this.source);

    final Collection<MutationDetails> availableMutations = m
        .findMutations(clazz);

    if (availableMutations.isEmpty()) {
      return availableMutations;
    } else {
      final ClassTree tree = ClassTree
          .fromBytes(this.source.getBytes(clazz.asJavaName()).get());

      this.interceptor.begin(tree);
      Collection<MutationDetails> updatedMutations = this.interceptor
          .intercept(availableMutations, m);
      this.interceptor.end();

      boolean hasTests = assignTestsToMutations(updatedMutations);

      LOG.info("Candidate mutations number:" + updatedMutations.size());
      LOG.info("Failing tests:" + failingTests);
      if (hasTests) {
        updatedMutations = selectMutationsCoveredByFailingTests(updatedMutations);
      }
      LOG.info("Filtered mutations number:" + updatedMutations.size());
      return updatedMutations;
    }
  }

  private boolean assignTestsToMutations(
      final Collection<MutationDetails> availableMutations) {
      boolean hasTests = false;
      for (final MutationDetails mutation : availableMutations) {
      final List<TestInfo> testDetails = this.testPrioritiser
          .assignTests(mutation);
      if (testDetails.size() > 0) {
        hasTests = true;
      }
      mutation.addTestsInOrder(testDetails);
      // LOG.info("Number of tests: " + mutation.getTestsInOrder().size());
    }
    return hasTests;
  }

  private Collection<MutationDetails> selectMutationsCoveredByFailingTests(Collection<MutationDetails> mutations) {
    if (failingTests == null || failingTests.size() == 0) {
      return mutations;
    }
    Collection<MutationDetails> filteredMutations = new ArrayList<>();
    for (MutationDetails mutation : mutations) {
      //format of test name: testClassName.testMethodName
      List<String> testsInOrder = mutation.getTestsInOrder().stream().map(ti -> ti.getName().substring(0, ti.getName().indexOf("("))).collect(Collectors.toList());
      // if (mutation.getLineNumber() == 191) {
      //   LOG.info(testsInOrder.toString());
      // }
      for (String failingTest : failingTests) {
        if (testsInOrder.contains(failingTest)) {
          // LOG.info(mutation.getClassLine().toString());
          filteredMutations.add(mutation);
          break;
        }
      }
    }

    return filteredMutations;
  }
}
