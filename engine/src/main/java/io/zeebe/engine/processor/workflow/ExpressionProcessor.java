package io.zeebe.engine.processor.workflow;

import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import io.zeebe.el.ExpressionLanguage;
import io.zeebe.engine.state.instance.VariablesState;
import io.zeebe.protocol.record.value.ErrorType;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public final class ExpressionProcessor {

  private final ExpressionLanguage expressionLanguage;
  private final VariablesState variablesState;

  public ExpressionProcessor(
      final ExpressionLanguage expressionLanguage, final VariablesState variablesState) {
    this.expressionLanguage = expressionLanguage;
    this.variablesState = variablesState;
  }

  public DirectBuffer evaluateStringExpression(
      final Expression expression, final BpmnStepContext<?> context) {

    final var evaluationResult = evaluateExpression(expression, context.getKey());

    if (evaluationResult.isFailure()) {
      context.raiseIncident(
          ErrorType.EXTRACT_VALUE_ERROR,
          String.format(
              "Failed to evaluate expression '%s': %s",
              evaluationResult.getExpression(), evaluationResult.getFailureMessage()));
      return null;
    }

    final var result = evaluationResult.asString();

    if (result == null) {
      context.raiseIncident(
          ErrorType.EXTRACT_VALUE_ERROR,
          String.format(
              "Expected result of the expression '%s' to be a STRING, but found '%s'.",
              evaluationResult.getExpression(), evaluationResult.getType()));
      return null;
    }

    return result;
  }

  private EvaluationResult evaluateExpression(
      final Expression expression, final long variableScopeKey) {

    if (expression.isStatic()) {
      return expressionLanguage.evaluateExpression(expression, new UnsafeBuffer());

    } else {
      final var variables = variablesState.getVariablesAsDocument(variableScopeKey);
      return expressionLanguage.evaluateExpression(expression, variables);
    }
  }
}
