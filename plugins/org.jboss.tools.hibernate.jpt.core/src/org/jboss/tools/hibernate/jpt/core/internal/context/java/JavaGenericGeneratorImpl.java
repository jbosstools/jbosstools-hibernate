/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.AbstractJavaGenerator;

/**
 * @author Dmitry Geraskov
 * 
 */
public class JavaGenericGeneratorImpl extends AbstractJavaGenerator 
											implements JavaGenericGenerator {
	
	private String strategy;

	/**
	 * @param parent
	 */
	public JavaGenericGeneratorImpl(JavaJpaContextNode parent) {
		super(parent);
	}
	
	@Override
	protected GenericGeneratorAnnotation getGeneratorResource() {
		return (GenericGeneratorAnnotation) super.getGeneratorResource();
	}

	public Integer getDefaultInitialValue() {
		return GenericGenerator.DEFAULT_INITIAL_VALUE;
	}
	
	public void initializeFromResource(GenericGeneratorAnnotation generator) {
		super.initializeFromResource(generator);
		this.strategy = generator.getStrategy();	
	}

	public void update(GenericGeneratorAnnotation generator) {
		super.update(generator);
		setSpecifiedStrategy_(generator.getStrategy());
	}
	
	public String getStrategy() {
		return strategy;
	}

	public void setSpecifiedStrategy(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		getGeneratorResource().setStrategy(strategy);
		firePropertyChanged(GENERIC_STRATEGY_PROPERTY, oldStrategy, strategy);
	}
	
	protected void setSpecifiedStrategy_(String strategy) {
		String oldStrategy = this.strategy;
		this.strategy = strategy;
		firePropertyChanged(GENERIC_STRATEGY_PROPERTY, oldStrategy, strategy);
	}

}
