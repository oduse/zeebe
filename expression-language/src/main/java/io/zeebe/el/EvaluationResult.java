package io.zeebe.el;

import org.agrona.DirectBuffer;

public interface EvaluationResult {

  String getType();

  DirectBuffer asString();

  boolean isFailure();

  String getFailureMessage();
}
