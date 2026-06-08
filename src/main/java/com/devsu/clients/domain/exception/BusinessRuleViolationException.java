package com.devsu.clients.domain.exception;


public class BusinessRuleViolationException extends DomainException {

    private final String ruleCode;

    public BusinessRuleViolationException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }
}