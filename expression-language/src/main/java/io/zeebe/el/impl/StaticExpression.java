package io.zeebe.el.impl;

import io.zeebe.el.Expression;

public final class StaticExpression implements Expression {

  private final String expression;

  public StaticExpression(final String expression) {
    this.expression = expression;
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
}
