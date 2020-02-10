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
import io.zeebe.el.ResultType;
import io.zeebe.util.buffer.BufferUtil;
import org.agrona.DirectBuffer;

public final class FeelEvaluationResult implements EvaluationResult {

  private final Expression expression;
  private final ResultType resultType;
  private final Object result;

  public FeelEvaluationResult(
      final Expression expression, final ResultType resultType, final Object result) {
    this.expression = expression;
    this.resultType = resultType;
    this.result = result;
  }

  @Override
  public String getExpression() {
    return expression.getExpression();
  }

  @Override
  public ResultType getType() {
    return resultType;
  }

  @Override
  public DirectBuffer asString() {
    if (resultType == ResultType.STRING) {
      return BufferUtil.wrapString(result.toString());
    }
    return null;
  }

  @Override
  public boolean isFailure() {
    return false;
  }

  @Override
  public String getFailureMessage() {
    return null;
  }
}
