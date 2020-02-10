package io.zeebe.el;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ExpressionLanguageTest {

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
            "Expected expression (e.g. '${variableName}') or static value (e.g. 'jobType') but found 'x.y'");
  }
}
