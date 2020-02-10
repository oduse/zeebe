package io.zeebe.el.impl;

import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import io.zeebe.util.buffer.BufferUtil;
import org.agrona.DirectBuffer;

public final class FeelEvaluationResult implements EvaluationResult {

  private final Expression expression;
  private final Object result;

  public FeelEvaluationResult(final Expression expression, final Object result) {
    this.expression = expression;
    this.result = result;
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
    if (result instanceof String) {
      return BufferUtil.wrapString((String) result);
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
