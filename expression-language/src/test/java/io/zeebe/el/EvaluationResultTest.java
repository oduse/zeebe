/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.el;

import static io.zeebe.test.util.MsgPackUtil.asMsgPack;
import static io.zeebe.util.buffer.BufferUtil.wrapString;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.agrona.DirectBuffer;
import org.junit.Test;

public class EvaluationResultTest {

  private static final DirectBuffer NO_VARIABLES = asMsgPack(Map.of());

  private final ExpressionLanguage expressionLanguage =
      ExpressionLanguageFactory.createExpressionLanguage();

  @Test
  public void staticString() {
    final var expression = expressionLanguage.parseExpression("x");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    assertThat(evaluationResult.getType()).isEqualTo(ResultType.STRING);
    assertThat(evaluationResult.asString()).isEqualTo(wrapString("x"));
  }

  @Test
  public void stringExpression() {
    final var expression = expressionLanguage.parseExpression("${\"x\"}");
    final var evaluationResult = expressionLanguage.evaluateExpression(expression, NO_VARIABLES);

    assertThat(evaluationResult.getType()).isEqualTo(ResultType.STRING);
    assertThat(evaluationResult.asString()).isEqualTo(wrapString("x"));
  }
}
