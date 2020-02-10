package io.zeebe.el;

import org.agrona.DirectBuffer;

public interface ExpressionLanguage {

  Expression parseExpression(String expression);

  EvaluationResult evaluateExpression(Expression expression, DirectBuffer variables);
}
