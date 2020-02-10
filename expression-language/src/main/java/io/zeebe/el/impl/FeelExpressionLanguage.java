package io.zeebe.el.impl;

import static io.zeebe.util.EnsureUtil.ensureNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zeebe.el.EvaluationResult;
import io.zeebe.el.Expression;
import io.zeebe.el.ExpressionLanguage;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import org.agrona.DirectBuffer;
import org.camunda.feel.FeelEngine;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public final class FeelExpressionLanguage implements ExpressionLanguage {

  private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(.+)\\}");
  private static final Pattern STATIC_VALUE_PATTERN = Pattern.compile("[a-zA-Z]+");

  private static final FeelEngine feelEngine = new FeelEngine.Builder().build();

  @Override
  public Expression parseExpression(final String expression) {
    ensureNotNull("expression", expression);

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
    ensureNotNull("expression", expression);
    ensureNotNull("variables", variables);

    if (!expression.isValid()) {
      final var failureMessage = expression.getFailureMessage();
      return new EvaluationFailure(expression, failureMessage);

    } else if (expression instanceof StaticExpression) {
      final var staticExpression = (StaticExpression) expression;
      return staticExpression;

    } else if (expression instanceof FeelExpression) {
      final var feelExpression = (FeelExpression) expression;
      return evaluateFeelExpression(expression, variables, feelExpression);
    }

    throw new IllegalArgumentException(
        String.format("Expected FEEL expression or static value but found '%s'", expression));
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

  private EvaluationResult evaluateFeelExpression(
      final Expression expression,
      final DirectBuffer variables,
      final FeelExpression feelExpression) {
    final var parsedExpression = feelExpression.getParsedExpression();

    final var objectMapper = new ObjectMapper(new MessagePackFactory());
    try {
      final var variablesAsMap =
          objectMapper.readValue(
              variables.byteArray(), new TypeReference<Map<String, Object>>() {});

      final var evalResult = feelEngine.eval(parsedExpression, variablesAsMap);

      if (evalResult.isLeft()) {
        final var failure = evalResult.left().get();
        return new EvaluationFailure(expression, failure.message());

      } else {
        final var result = evalResult.right().get();
        return new FeelEvaluationResult(expression, result);
      }

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
