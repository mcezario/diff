package org.mcezario.diff.domains;


import org.mcezario.diff.domains.exceptions.BusinessRuleException;
import org.mcezario.diff.domains.exceptions.ExceptionId;

public class TestBusinessException extends BusinessRuleException {

  private static final long serialVersionUID = -7516931773313479839L;

  public TestBusinessException() {
    super(ExceptionId.create("test.error", "Test message error"));
  }
  
}
