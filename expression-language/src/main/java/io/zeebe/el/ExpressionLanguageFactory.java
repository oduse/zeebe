package io.zeebe.el;

import io.zeebe.el.impl.FeelExpressionLanguage;

public class ExpressionLanguageFactory {

  public static ExpressionLanguage createExpressionLanguage() {
    return new FeelExpressionLanguage();
  }
}
