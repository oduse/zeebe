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
import io.zeebe.util.buffer.BufferUtil;
import org.agrona.DirectBuffer;

public final class StaticExpression implements Expression, EvaluationResult {

  private final String expression;
  private final DirectBuffer result;

  public StaticExpression(final String expression) {
    this.expression = expression;
    result = BufferUtil.wrapString(expression);
  }

  @Override
  public String getExpression() {
    return expression;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public String getFailureMessage() {
    return null;
  }

  @Override
  public String getType() {
    return null;
  }

  @Override
  public DirectBuffer asString() {
    return result;
  }

  @Override
  public boolean isFailure() {
    return false;
  }
}
