package net.waymire.tyranny.common.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.waymire.tyranny.common.protocol.AuthControlOpcode;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AuthControlProtocolProcessor {
	public AuthControlOpcode opcode();
}