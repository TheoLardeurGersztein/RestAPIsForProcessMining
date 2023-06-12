package org.processmining.models.jgraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;



public class ProMJGraphVisualizer {
    private static ProMJGraphVisualizer instance = null;

    protected ProMJGraphVisualizer() {
    }

    public static ProMJGraphVisualizer instance() {
        if (instance == null) {
            instance = new ProMJGraphVisualizer();
        }

        return instance;
    }

    protected GraphLayoutConnection findConnection(PluginContext context, DirectedGraph<?, ?> graph) {
        return this.findConnection(context.getConnectionManager(), graph);
    }

    protected GraphLayoutConnection findConnection(ConnectionManager manager, DirectedGraph<?, ?> graph) {
        Collection<ConnectionID> cids = manager.getConnectionIDs();
        Iterator i$ = cids.iterator();

        while(i$.hasNext()) {
            ConnectionID id = (ConnectionID)i$.next();

            Connection c;
            try {
                c = manager.getConnection(id);
            } catch (ConnectionCannotBeObtained var8) {
                continue;
            }

            if (c != null && !c.isRemoved() && c instanceof GraphLayoutConnection && c.getObjectWithRole("graph") == graph) {
                return (GraphLayoutConnection)c;
            }
        }

        return null;
    }

    public ProMJGraphPanel visualizeGraphWithoutRememberingLayout(DirectedGraph<?, ?> graph) {
        return this.visualizeGraph(new GraphLayoutConnection(graph), (PluginContext)null, graph, new ViewSpecificAttributeMap());
    }

    public ProMJGraphPanel visualizeGraphWithoutRememberingLayout(DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
        return this.visualizeGraph(new GraphLayoutConnection(graph), (PluginContext)null, graph, map);
    }

    public ProMJGraphPanel visualizeGraph(PluginContext context, DirectedGraph<?, ?> graph) {
        return this.visualizeGraph(this.findConnection(context, graph), context, graph, new ViewSpecificAttributeMap());
    }

    public ProMJGraphPanel visualizeGraph(PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
        return this.visualizeGraph(this.findConnection(context, graph), context, graph, map);
    }

    public ProMJGraph visualizeGraph2(PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
        return this.visualizeGraphTest(this.findConnection(context, graph), context, graph, map);
    }

    private ProMJGraphPanel visualizeGraph(GraphLayoutConnection layoutConnection, PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
        boolean newConnection = false;
        if (layoutConnection == null) {
            layoutConnection = this.createLayoutConnection(graph);
            newConnection = true;
        }

        if (!layoutConnection.isLayedOut()) {
            layoutConnection.expandAll();
        }

        // LAAAAAAAAAAAAAAAAAAAAAA
        ProMGraphModel model = new ProMGraphModel(graph);
        //
        ProMJGraph jgraph;
        synchronized(instance) {
            jgraph = new ProMJGraph(model, map, layoutConnection);
        }

        JGraphLayout layout = this.getLayout((Integer)map.get(graph, "ProM_Vis_attr_orientation", 5));
        if (!layoutConnection.isLayedOut()) {
            JGraphFacade facade = new JGraphFacade(jgraph);
            facade.setOrdered(false);
            facade.setEdgePromotion(true);
            facade.setIgnoresCellsInGroups(false);
            facade.setIgnoresHiddenCells(false);
            facade.setIgnoresUnconnectedCells(false);
            facade.setDirected(true);
            facade.resetControlPoints();
            if (layout instanceof JGraphHierarchicalLayout) {
                facade.run((JGraphHierarchicalLayout)layout, true);
            } else {
                facade.run(layout, true);
            }

            Map<?, ?> nested = facade.createNestedMap(true, true);
            jgraph.getGraphLayoutCache().edit(nested);
            layoutConnection.setLayedOut(true);
        }

        jgraph.setUpdateLayout(layout);
        ProMJGraphPanel panel = new ProMJGraphPanel(jgraph);
        //panel.addViewInteractionPanel(new PIPInteractionPanel(panel), 1);
        //panel.addViewInteractionPanel(new ZoomInteractionPanel(panel, 250), 7);
        //panel.addViewInteractionPanel(new ExportInteractionPanel(panel), 5);
        layoutConnection.updated();
        if (newConnection) {
            context.getConnectionManager().addConnection(layoutConnection);
        }

        return panel;
    }

    private ProMJGraph visualizeGraphTest(GraphLayoutConnection layoutConnection, PluginContext context, DirectedGraph<?, ?> graph, ViewSpecificAttributeMap map) {
        boolean newConnection = false;
        if (layoutConnection == null) {
            layoutConnection = this.createLayoutConnection(graph);
            newConnection = true;
        }

        if (!layoutConnection.isLayedOut()) {
            layoutConnection.expandAll();
        }

        // LAAAAAAAAAAAAAAAAAAAAAA
        ProMGraphModel model = new ProMGraphModel(graph);
        //
        ProMJGraph jgraph;
        synchronized(instance) {
            jgraph = new ProMJGraph(model, map, layoutConnection);
        }

        return jgraph;
    }

    private GraphLayoutConnection createLayoutConnection(DirectedGraph<?, ?> graph) {
        GraphLayoutConnection c = new GraphLayoutConnection(graph);
        return c;
    }

    protected JGraphLayout getLayout(int orientation) {
        JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
        layout.setDeterministic(true);
        layout.setCompactLayout(true);
        layout.setFineTuning(true);
        layout.setParallelEdgeSpacing(15.0);
        layout.setFixRoots(false);
        layout.setOrientation(orientation);
        return layout;
    }
}
