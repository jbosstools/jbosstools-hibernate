package org.jboss.tools.hibernate.jpt.core.internal.context;

import java.util.ListIterator;

import org.eclipse.jpt.core.context.GeneratorContainer;

public interface HibernateGeneratorContainer extends GeneratorContainer {
	
	//******************** generic generator *****************
	
	String GENERIC_GENERATORS_LIST = "genericGenerators"; //$NON-NLS-1$	
	
	/**
	 * Return a list iterator of the generic generators.
	 * This will not be null.
	 */
	ListIterator<? extends GenericGenerator> genericGenerators();
	
	/**
	 * Return the number of generic generators.
	 */
	int genericGeneratorsSize();

	/**
	 * Add a generic generator to the entity return the object representing it.
	 */
	GenericGenerator addGenericGenerator(int index);
	
	/**
	 * Remove the generic generator at the index from the entity.
	 */
	void removeGenericGenerator(int index);
	
	/**
	 * Remove the generic generator from the entity.
	 */
	void removeGenericGenerator(GenericGenerator generator);
	
	/**
	 * Move the generic generator from the source index to the target index.
	 */
	void moveGenericGenerator(int targetIndex, int sourceIndex);

}
