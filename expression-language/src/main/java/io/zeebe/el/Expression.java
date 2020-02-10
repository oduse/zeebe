package io.zeebe.el;

public interface Expression {

  String getExpression();

  boolean isStatic();

  boolean isValid();

  String getFailureMessage();
}
