/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph.layout;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;

/**
 * Extended version of DirectedGraphLayout which allows DirectedGraphLayout
 * functionality to be used even when graph nodes either have no edges, or when part
 * of clusters isolated from other clusters of Nodes
 * 
 * @author Phil Zoio
 */
public class NodeJoiningDirectedGraphLayout extends DirectedGraphLayout
{

	/**
	 * @param graph public method called to handle layout task
	 */
	public void visit(DirectedGraph graph)
	{
		
		//System.out.println("Before Populate: Graph nodes: " + graph.nodes);
		//System.out.println("Before Populate: Graph edges: " + graph.edges);				
		
		//add dummy edges so that graph does not fall over because some nodes
		// are not in relationships
		new DummyEdgeCreator().visit(graph);
		
		// create edges to join any isolated clusters
		new ClusterEdgeCreator().visit(graph);	
		
		//System.out.println("After Populate: Graph nodes: " + graph.nodes);
		//System.out.println("After Populate: Graph edges: " + graph.edges);	
		
		
		super.visit(graph);
	}
}