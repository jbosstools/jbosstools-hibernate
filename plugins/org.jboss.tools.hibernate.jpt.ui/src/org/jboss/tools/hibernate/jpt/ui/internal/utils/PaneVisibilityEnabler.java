/*******************************************************************************
 * Copyright (c) 2008, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.utils;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.collection.CollectionTools;
import org.eclipse.jpt.common.utility.internal.iterator.IteratorTools;
import org.eclipse.jpt.common.utility.internal.transformer.TransformerAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.transformer.Transformer;

/**
 * This <code>PaneVisibilityEnabler</code> keeps the "visible" state of a
 * collection of controls in synch with the provided boolean holder.
 *
 * @version 2.0
 * @since 2.0
 */
public class PaneVisibilityEnabler extends StateController
{
	/**
	 * Creates a new <code>PaneVisibilityEnabler</code> with a default value of
	 * <code>false</code> (i.e. not visible).
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param pane The pane whose "visible" state is kept in sync with the
	 * boolean holder's value
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Pane<?> pane) {

		this(booleanHolder, pane, false);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code> with a default value of
	 * <code>false</code> (i.e. not visible).
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes The collection of panes whose "visible" state is kept in sync
	 * with the boolean holder's value
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Pane<?>... panes) {

		this(booleanHolder, CollectionTools.collection(panes), false);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code>.
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param pane The pane whose "visible" state is kept in sync with the
	 * boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Pane<?> pane,
	                             boolean defaultValue) {

		this(booleanHolder, IteratorTools.singletonIterator(pane), false);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code>.
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes The collection of panes whose "visible" state is kept in sync
	 * with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Pane<?>[] panes,
	                             boolean defaultValue) {

		this(booleanHolder, IteratorTools.iterator(panes), defaultValue);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code> with a default value of
	 * <code>false</code> (i.e. not visible).
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes The collection of panes whose "visible" state is kept in sync
	 * with the boolean holder's value
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Collection<? extends Pane<?>> panes) {

		this(booleanHolder, panes, false);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code>.
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes The collection of panes whose "visible" state is kept in sync
	 * with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Collection<? extends Pane<?>> panes,
	                             boolean defaultValue) {

		this(booleanHolder, panes.iterator(), defaultValue);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code> with a default value of
	 * <code>false</code> (i.e. not visible).
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes An iterator on the collection of panes whose "visible" state
	 * is kept in sync with the boolean holder's value
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Iterator<? extends Pane<?>> panes) {

		this(booleanHolder, panes, false);
	}

	/**
	 * Creates a new <code>PaneVisibilityEnabler</code>.
	 *
	 * @param booleanHolder A value model on the underlying boolean model
	 * @param panes An iterator on the collection of panes whose "visible" state
	 * is kept in sync with the boolean holder's value
	 * @param defaultValue The value to use when the underlying model is
	 * <code>null</code>
	 */
	public PaneVisibilityEnabler(PropertyValueModel<Boolean> booleanHolder,
	                             Iterator<? extends Pane<?>> panes,
	                             boolean defaultValue) {

		super(booleanHolder, wrap(panes), defaultValue);
	}

	private static Collection<ControlHolder> wrap(Iterator<? extends Pane<?>> panes) {
		return CollectionTools.collection(IteratorTools.transform(panes, PANE_HOLDER_TRANSFORMER));
	}

	private static final Transformer<Pane<?>, ControlHolder> PANE_HOLDER_TRANSFORMER = new PaneHolderTransformer();
	/* CU private */ static class PaneHolderTransformer
		extends TransformerAdapter<Pane<?>, ControlHolder>
	{
		@Override
		public ControlHolder transform(Pane<?> pane) {
			return new PaneHolder(pane);
		}
	}

	/**
	 * This holder holds onto an <code>Pane</code> and update its visible
	 * state.
	 */
	private static class PaneHolder implements ControlHolder {
		private final Pane<?> pane;

		PaneHolder(Pane<?> pane) {
			super();
			this.pane = pane;
		}

		public void updateState(boolean state) {
			this.pane.setVisible(state);
		}
	}
}
