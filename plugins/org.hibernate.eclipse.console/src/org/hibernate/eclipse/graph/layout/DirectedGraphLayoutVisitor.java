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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.hibernate.eclipse.graph.parts.AssociationEditPart;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;

public class DirectedGraphLayoutVisitor
{

	Map<AbstractGraphicalEditPart, Object> partToNodesMap;
	Set<AssociationEditPart> addedAssociations;
	
	DirectedGraph graph;
	private ConfigurationEditPart diagram;

	/**
	 * Public method for reading graph nodes
	 */
	public void layoutDiagram(ConfigurationEditPart diagram)
	{

		partToNodesMap = new HashMap<AbstractGraphicalEditPart, Object>();
		addedAssociations = new HashSet<AssociationEditPart>();
		this.diagram = diagram;
		graph = new DirectedGraph();
		addNodes(diagram);
		if (graph.nodes.size() > 0)
		{	
			addEdges(diagram);
			new NodeJoiningDirectedGraphLayout().visit(graph);
			applyResults(diagram);
		}

	}

	//******************* ConfigurationEditPart contribution methods **********/

	protected void addNodes(ConfigurationEditPart diagram)
	{
		GraphAnimation.recordInitialState(diagram.getFigure());
		IFigure fig = diagram.getFigure();
		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			PersistentClassEditPart tp = (PersistentClassEditPart) diagram.getChildren().get(i);
			addNodes(tp);
		}
	}

	/**
	 * Adds nodes to the graph object for use by the GraphLayoutManager
	 */
	protected void addNodes(PersistentClassEditPart pClassPart)
	{
		Node n = new Node(pClassPart);
		n.width = pClassPart.getFigure().getPreferredSize(400, 300).width;
		n.height = pClassPart.getFigure().getPreferredSize(400, 300).height;
		n.setPadding(new Insets(50));
		partToNodesMap.put(pClassPart, n);
		graph.nodes.add(n);
	}

	protected void addEdges(ConfigurationEditPart diagram)
	{
		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			PersistentClassEditPart classPart = (PersistentClassEditPart) diagram.getChildren().get(i);
			addEdges(classPart);
		}
	}

	//******************* PersistentClassEditPart contribution methods **********/

	@SuppressWarnings("unchecked")
	protected void addEdges(PersistentClassEditPart classPart)
	{
		List<AssociationEditPart> outgoing = classPart.getSourceConnections();
		for (AssociationEditPart relationshipPart : outgoing) {
			addEdges(relationshipPart);
		}
	}

	//******************* RelationshipPart contribution methods **********/

	protected void addEdges(AssociationEditPart relationshipPart)
	{
		GraphAnimation.recordInitialState((Connection) relationshipPart.getFigure());
		Node source = (Node) partToNodesMap.get(relationshipPart.getSource());
		Node target = (Node) partToNodesMap.get(relationshipPart.getTarget());
		if(source==target) {
			/*if(addedAssociations.contains(relationshipPart)) {
				System.out.println("Ignoring: " + relationshipPart);
				return;
			} else {
				System.out.println("Adding: " + relationshipPart);
				addedAssociations.add(relationshipPart);
			}	*/
			return;
		}
		
		if(source==null || target == null) {
			return;
		}
		
		Edge e = new Edge(relationshipPart, source, target);
		e.weight = 2;
		graph.edges.add(e);
		partToNodesMap.put(relationshipPart, e);
	}

	//******************* ConfigurationEditPart apply methods **********/

	protected void applyResults(ConfigurationEditPart diagram)
	{
		applyChildrenResults(diagram);
	}

	protected void applyChildrenResults(ConfigurationEditPart diagram)
	{
		for (int i = 0; i < diagram.getChildren().size(); i++)
		{
			PersistentClassEditPart pClassPart = (PersistentClassEditPart) diagram.getChildren().get(i);
			applyResults(pClassPart);
		}
	}

	protected void applyOwnResults(ConfigurationEditPart diagram)
	{
	}

	//******************* PersistentClassEditPart apply methods **********/

	public void applyResults(PersistentClassEditPart pClassPart)
	{

		Node n = (Node) partToNodesMap.get(pClassPart);
		Figure classFigure = (Figure) pClassPart.getFigure();

		// switched x/y to make it go left to right
		Rectangle bounds = new Rectangle(n.y, n.x, classFigure.getPreferredSize().width,
				classFigure.getPreferredSize().height);

		classFigure.setBounds(bounds);

		for (int i = 0; i < pClassPart.getSourceConnections().size(); i++)
		{
			AssociationEditPart relationship = (AssociationEditPart) pClassPart.getSourceConnections().get(i);
			applyResults(relationship);
		}
	}

	//******************* RelationshipPart apply methods **********/

	protected void applyResults(AssociationEditPart relationshipPart)
	{

		Edge e = (Edge) partToNodesMap.get(relationshipPart);
		PolylineConnection conn = (PolylineConnection) relationshipPart.getConnectionFigure();
		
		if (e==null) {
			//conn.setConnectionRouter(new ShortestPathConnectionRouter(diagram.getFigure()));
			return;
		}
		NodeList nodes = e.vNodes;

		
		
		if (nodes != null)
		{
			List<AbsoluteBendpoint> bends = new ArrayList<AbsoluteBendpoint>();
			for (int i = 0; i < nodes.size(); i++)
			{
				Node vn = nodes.getNode(i);
				int x = vn.x;
				int y = vn.y;
				if (e.isFeedback)
				{
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
					bends.add(new AbsoluteBendpoint(x, y));

				}
				else
				{
					bends.add(new AbsoluteBendpoint(x, y));
					bends.add(new AbsoluteBendpoint(x, y + vn.height));
				}
			}
			conn.setRoutingConstraint(bends);
		}
		else
		{
			conn.setRoutingConstraint(Collections.EMPTY_LIST);
		}

	}

}