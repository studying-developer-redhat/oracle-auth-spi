package com.redhat.rhsso.spi.exception;

public class BusinessException extends Exception {
	
	private static final long serialVersionUID = -7757794066984878495L;

	public BusinessException(String message) {
		super(message);
	}
}