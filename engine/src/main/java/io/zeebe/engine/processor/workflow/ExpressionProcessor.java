/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.engine.processor.workflow;

import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import io.zeebe.el.ExpressionLanguage;
import io.zeebe.el.ResultType;
import io.zeebe.engine.state.instance.VariablesState;
import io.zeebe.protocol.record.value.ErrorType;
import java.util.function.Function;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public final class ExpressionProcessor {

  private static final DirectBuffer NO_VARIABLES = new UnsafeBuffer();

  private final ExpressionLanguage expressionLanguage;
  private final VariablesState variablesState;

  public ExpressionProcessor(
      final ExpressionLanguage expressionLanguage, final VariablesState variablesState) {
    this.expressionLanguage = expressionLanguage;
    this.variablesState = variablesState;
  }

  public DirectBuffer evaluateStringExpression(
      final Expression expression, final BpmnStepContext<?> context) {
    return evaluateExpression(expression, context, EvaluationResult::asString, ResultType.STRING);
  }

  private DirectBuffer evaluateExpression(
      final Expression expression,
      final BpmnStepContext<?> context,
      final Function<EvaluationResult, DirectBuffer> resultExtractor,
      final ResultType expectedResultType) {

    final var evaluationResult = evaluateExpression(expression, context.getKey());

    if (evaluationResult.isFailure()) {
      context.raiseIncident(
          ErrorType.EXTRACT_VALUE_ERROR,
          String.format(
              "Failed to evaluate expression '%s': %s",
              evaluationResult.getExpression(), evaluationResult.getFailureMessage()));
      return null;
    }

    final var result = resultExtractor.apply(evaluationResult);

    if (result == null) {
      context.raiseIncident(
          ErrorType.EXTRACT_VALUE_ERROR,
          String.format(
              "Expected result of the expression '%s' to be '%s', but found '%s'.",
              evaluationResult.getExpression(), expectedResultType, evaluationResult.getType()));
      return null;
    }

    return result;
  }

  private EvaluationResult evaluateExpression(
      final Expression expression, final long variableScopeKey) {

    if (expression.isStatic()) {
      return expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    } else {
      final var variables = variablesState.getVariablesAsDocument(variableScopeKey);
      return expressionLanguage.evaluateExpression(expression, variables);
    }
  }
}
