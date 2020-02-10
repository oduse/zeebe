/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.el;

import static io.zeebe.test.util.MsgPackUtil.asMsgPack;
import static org.assertj.core.api.Assertions.assertThat;

import io.zeebe.util.buffer.BufferUtil;
import java.util.Map;
import org.agrona.DirectBuffer;
import org.junit.Test;

public class ExpressionLanguageTest {

  private static final DirectBuffer NO_VARIABLES = asMsgPack(Map.of());

  private final ExpressionLanguage expressionLanguage =
      ExpressionLanguageFactory.createExpressionLanguage();

  @Test
  public void shouldParseStaticValue() {
    final var expression = expressionLanguage.parseExpression("x");

    assertThat(expression).isNotNull();
    assertThat(expression.isStatic()).isTrue();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("x");
  }

  @Test
  public void shouldParseExpression() {
    final var expression = expressionLanguage.parseExpression("${x.y}");

    assertThat(expression).isNotNull();
    assertThat(expression.isStatic()).isFalse();
    assertThat(expression.isValid()).isTrue();
    assertThat(expression.getExpression()).isEqualTo("x.y");
  }

  @Test
  public void shouldParseInvalidExpression() {
    final var expression = expressionLanguage.parseExpression("${x == 5}");

    assertThat(expression).isNotNull();
    assertThat(expression.isValid()).isFalse();
    assertThat(expression.getExpression()).isEqualTo("x == 5");
    assertThat(expression.getFailureMessage())
        .startsWith("failed to parse expression 'x == 5': [1.4] error:");
  }

  @Test
  public void shouldParseExpressionWithInvalidFormat() {
    final var expression = expressionLanguage.parseExpression("x.y");

    assertThat(expression).isNotNull();
    assertThat(expression.isValid()).isFalse();
    assertThat(expression.getExpression()).isEqualTo("x.y");
    assertThat(expression.getFailureMessage())
        .isEqualTo(
            "Expected FEEL expression (e.g. '${variableName}') or static value (e.g. 'jobType') but found 'x.y'");
  }

  @Test
  public void shouldEvaluateStaticValue() {
    final var expression = expressionLanguage.parseExpression("x");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.asString()).isEqualTo(BufferUtil.wrapString("x"));
  }

  @Test
  public void shouldEvaluateExpression() {
    final var expression = expressionLanguage.parseExpression("${x}");
    final var evaluationResult =
        expressionLanguage.evaluateExpression(expression, asMsgPack(Map.of("x", "x")));

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isFalse();
    assertThat(evaluationResult.asString()).isEqualTo(BufferUtil.wrapString("x"));
  }

  @Test
  public void shouldEvaluateExpressionWithMissingVariables() {
    final var expression = expressionLanguage.parseExpression("${x}");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isTrue();
    assertThat(evaluationResult.getFailureMessage())
        .startsWith("failed to evaluate expression 'x': no variable found for name 'x'");
  }

  @Test
  public void shouldEvaluateInvalidExpression() {
    final var expression = expressionLanguage.parseExpression("x.y");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    assertThat(evaluationResult).isNotNull();
    assertThat(evaluationResult.isFailure()).isTrue();
    assertThat(evaluationResult.getFailureMessage()).startsWith("Expected FEEL expression");
  }
}
