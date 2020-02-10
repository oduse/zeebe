/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.el.impl;

import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import org.agrona.DirectBuffer;

public final class EvaluationFailure implements EvaluationResult {

  private final Expression expression;
  private final String failureMessage;

  public EvaluationFailure(final Expression expression, final String failureMessage) {
    this.expression = expression;
    this.failureMessage = failureMessage;
  }

  @Override
  public String getExpression() {
    return expression.getExpression();
  }

  @Override
  public String getType() {
    return null;
  }

  @Override
  public DirectBuffer asString() {
    return null;
  }

  @Override
  public boolean isFailure() {
    return true;
  }

  @Override
  public String getFailureMessage() {
    return failureMessage;
  }
}
