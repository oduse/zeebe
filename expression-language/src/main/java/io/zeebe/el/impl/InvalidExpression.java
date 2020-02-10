package io.zeebe.el.impl;

import io.zeebe.el.Expression;

public final class InvalidExpression implements Expression {

  private final String expression;
  private final String failureMessage;

  public InvalidExpression(final String expression, final String failureMessage) {
    this.expression = expression;
    this.failureMessage = failureMessage;
  }

  @Override
  public String getExpression() {
    return expression;
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isValid() {
    return false;
  }

  @Override
  public String getFailureMessage() {
    return failureMessage;
  }
}
