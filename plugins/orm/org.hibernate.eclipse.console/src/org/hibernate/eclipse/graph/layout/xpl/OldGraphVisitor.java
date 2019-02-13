package org.hibernate.eclipse.graph.layout.xpl;

import org.eclipse.draw2d.graph.DirectedGraph;

public abstract class OldGraphVisitor {

		/**
		 * Act on the given directed graph.
		 * @param g the graph
		 */
		void visit(DirectedGraph g) { }

		/**
		 * Called in reverse order of visit.
		 * @since 3.1
		 * @param g the graph to act upon
		 */
		void revisit(DirectedGraph g) { }

}
