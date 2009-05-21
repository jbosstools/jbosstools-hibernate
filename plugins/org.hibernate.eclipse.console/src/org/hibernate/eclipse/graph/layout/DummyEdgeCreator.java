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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.hibernate.eclipse.graph.layout.xpl.OldGraphVisitor;

/**
 * Creates dummy edges between nodes, to be used with NodeJoiningDirectedGraphLayout
 * @author Phil Zoio
 */
public class DummyEdgeCreator extends OldGraphVisitor
{

	NodeList nodeList;
	EdgeList edgeList;
	DirectedGraph graph;

	List<Edge> edgesAdded;
	List<Node> candidateList;
	int targetNodeIndex;

	boolean cleanNextTime = false;

	/**
	 * @param clean
	 *            next time
	 */
	public void visit(DirectedGraph g)
	{
		cleanNextTime = true;
		init(g);
		setDummyEdges();
	}

	/**
	 * @param graph
	 */
	private void init(DirectedGraph graph)
	{

		this.graph = graph;
		this.nodeList = graph.nodes;
		this.edgeList = graph.edges;
		edgesAdded = new ArrayList<Edge>();

	}

	protected void setDummyEdges()
	{

		Node targetNode = null;

		int nodeCount = nodeList.size();

		//if node count is only one then we don't have to worry about whether
		// the nodes are connected
		if (nodeCount > 1)
		{
			for (Iterator<Node> iter = nodeList.iterator(); iter.hasNext();)
			{
				Node sourceNode = iter.next();

				//we will need to set up a dummy relationship for any table not
				// in one already
				if (sourceNode.outgoing.size() == 0 && sourceNode.incoming.size() == 0)
				{

					targetNode = findTargetNode(sourceNode);
					Edge edge = newDummyEdge(targetNode, sourceNode);
					edgesAdded.add(edge);

				}
			}
		}
	}

	/**
	 * creates a new dummy edge to be used in the graph
	 */
	private Edge newDummyEdge(Node targetNode, Node sourceNode)
	{
		DummyEdgePart edgePart = new DummyEdgePart();
		Edge edge = new Edge(edgePart, sourceNode, targetNode);
		edge.weight = 2;
		edgeList.add(edge);

		return edge;
	}

	/**
	 * @return a suitable first table to relate to. Will only be called if there
	 *         are > 1 table
	 */
	private Node findTargetNode(Node cantBeThis)
	{

		if (candidateList == null)
		{

			candidateList = new NodeList();

			boolean relationshipFound = false;

			//first look for set of targets which are already in relationships
			for (Iterator<Node> iter = nodeList.iterator(); iter.hasNext();)
			{
				Node element = iter.next();
				if ((element.incoming.size() + element.outgoing.size()) >= 1)
				{
					candidateList.add(element);
					relationshipFound = true;
				}
			}

			//if none found, then just use the existing set
			if (!relationshipFound)
			{
				candidateList = nodeList;
			}
			// sort the target set with those in fewest relationships coming
			// first
			else
			{

				Comparator<Node> comparator = new Comparator<Node>()
				{

					public int compare(Node t1, Node t2)
					{
						return t1.incoming.size() - (t2.incoming.size());
					}

				};

				try
				{
					Collections.sort(candidateList, comparator);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				//System.out.println("Sorted set: " + candidateList);

			}

		}

		//handle situation where first table is the top of the set - we will
		// want the next one then
		Node toReturn = getNext();
		if (toReturn == cantBeThis)
		{
			toReturn = getNext();
		}
		return toReturn;

	}

	private Node getNext()
	{
		if (targetNodeIndex == candidateList.size() - 1)
			targetNodeIndex = 0;
		else
			targetNodeIndex++;

		return candidateList.get(targetNodeIndex);
	}

	protected void removeDummyEdges()
	{
		for (Iterator<Edge> iter = edgesAdded.iterator(); iter.hasNext();)
		{
			Edge edge = iter.next();
			edgeList.remove(edge);

		}
	}

}