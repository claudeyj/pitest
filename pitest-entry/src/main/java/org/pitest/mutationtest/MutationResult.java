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
package org.pitest.mutationtest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.pitest.mutationtest.engine.MutationDetails;

public final class MutationResult {

  private final MutationDetails        details;
  private final MutationStatusTestPair status;

  public MutationResult(final MutationDetails md,
      final MutationStatusTestPair status) {
    this.details = md;
    this.status = status;
  }

  public MutationDetails getDetails() {
    return this.details;
  }

  public Optional<String> getKillingTest() {
    return this.status.getKillingTest();
  }

  public List<String> getKillingTests() {
    return this.status.getKillingTests();
  }

  public List<String> getSucceedingTests() {
    return this.status.getSucceedingTests();
  }

  public List<String> getTimeoutTests() {
    return this.status.getTimeOuttests();
  }

  public List<String> getMemoryErrorTests() {
    return this.status.getMemoryErrorTests();
  }

  public List<String> getRunErrorTests() {
    return this.status.getRunErrorTests();
  }

  public DetectionStatus getStatus() {
    return this.status.getStatus();
  }

  public int getNumberOfTestsRun() {
    return this.status.getNumberOfTestsRun();
  }

  public MutationStatusTestPair getStatusTestPair() {
    return this.status;
  }

  public String getStatusDescription() {
    return getStatus().name();
  }

  public String getKillingTestDescription() {
    return getKillingTest().orElse("none");
  }

  @Override
  public int hashCode() {
    return Objects.hash(details, status);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MutationResult other = (MutationResult) obj;
    return Objects.equals(details, other.details)
            && Objects.equals(status, other.status);
  }

  @Override
  public String toString() {
    return "MutationResult [details=" + this.details + ", status="
        + this.status + "]";
  }

}