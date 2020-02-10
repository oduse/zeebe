/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.el;

import org.agrona.DirectBuffer;

public interface ExpressionLanguage {

  Expression parseExpression(String expression);

  EvaluationResult evaluateExpression(Expression expression, DirectBuffer variables);
}
