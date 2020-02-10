package io.zeebe.el.impl;

import io.zeebe.el.Expression;
import org.camunda.feel.ParsedExpression;

public final class FeelExpression implements Expression {

  private final ParsedExpression expression;

  public FeelExpression(final ParsedExpression expression) {
    this.expression = expression;
  }

  @Override
  public String getExpression() {
    return expression.text();
  }

  @Override
  public boolean isStatic() {
    return false;
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
