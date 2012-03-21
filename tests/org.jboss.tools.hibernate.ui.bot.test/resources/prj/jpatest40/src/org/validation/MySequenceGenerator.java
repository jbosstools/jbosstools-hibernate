package org.validation;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class MySequenceGenerator implements IdentifierGenerator {
	public static int unique; 
	public Serializable generate(SessionImplementor session, Object object) {
		return unique++;
	}
}