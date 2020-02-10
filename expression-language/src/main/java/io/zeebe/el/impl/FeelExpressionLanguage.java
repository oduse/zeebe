package io.zeebe.el.impl;

import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import io.zeebe.el.ExpressionLanguage;
import java.util.regex.Pattern;
import org.agrona.DirectBuffer;
import org.camunda.feel.FeelEngine;

public final class FeelExpressionLanguage implements ExpressionLanguage {

  private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(.+)\\}");
  private static final Pattern STATIC_VALUE_PATTERN = Pattern.compile("[a-zA-Z]+");

  private static final FeelEngine feelEngine = new FeelEngine.Builder().build();

  @Override
  public Expression parseExpression(final String expression) {

    final var expressionMatcher = EXPRESSION_PATTERN.matcher(expression);
    final var valueMather = STATIC_VALUE_PATTERN.matcher(expression);

    if (expressionMatcher.matches()) {
      final var unpackedExpression = expressionMatcher.group(1);
      return parseFeelExpression(unpackedExpression);

    } else if (valueMather.matches()) {
      final var value = valueMather.group();
      return new StaticExpression(value);

    } else {
      final var failureMessage =
          String.format(
              "Expected FEEL expression (e.g. '${variableName}') or static value (e.g. 'jobType') but found '%s'",
              expression);
      return new InvalidExpression(expression, failureMessage);
    }
  }

  @Override
  public EvaluationResult evaluateExpression(
      final Expression expression, final DirectBuffer variables) {
    return null;
  }

  private Expression parseFeelExpression(final String expression) {
    final var parseResult = feelEngine.parseExpression(expression);

    if (parseResult.isLeft()) {
      final var failure = parseResult.left().get();
      return new InvalidExpression(expression, failure.message());

    } else {
      final var parsedExpression = parseResult.right().get();
      return new FeelExpression(parsedExpression);
    }
  }
}
