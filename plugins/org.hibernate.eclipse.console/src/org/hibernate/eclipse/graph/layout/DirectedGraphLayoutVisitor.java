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
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.hibernate.eclipse.graph.parts.AssociationEditPart;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;

public class DirectedGraphLayoutVisitor
{

	Map partToNodesMap;
	Set addedAssociations;
	
	DirectedGraph graph;
	private ConfigurationEditPart diagram;

	/**
	 * Public method for reading graph nodes
	 */
	public void layoutDiagram(ConfigurationEditPart diagram)
	{

		partToNodesMap = new HashMap();
		addedAssociations = new HashSet();
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
		n.setPadding(new Insets(10, 8, 10, 12));
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

	protected void addEdges(PersistentClassEditPart classPart)
	{
		List outgoing = classPart.getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++)
		{
			AssociationEditPart relationshipPart = (AssociationEditPart) classPart.getSourceConnections().get(i);
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

		Rectangle bounds = new Rectangle(n.x, n.y, classFigure.getPreferredSize().width,
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
			List bends = new ArrayList();
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