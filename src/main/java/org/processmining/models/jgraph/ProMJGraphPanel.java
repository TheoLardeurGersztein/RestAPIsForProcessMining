package org.processmining.models.jgraph;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jgraph.graph.BasicMarqueeHandler;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;

public class ProMJGraphPanel extends ScalableViewPanel {
    private static final long serialVersionUID = 8937461038820086748L;

    public ProMJGraphPanel(final ProMJGraph graph) {
        super(graph);
        JLabel label = new JLabel("<html>&#8629;</html>");
    }

    protected void initialize() {
        this.getGraph().setTolerance(4);
        this.getGraph().setMarqueeHandler(new BasicMarqueeHandler() {
            private boolean test(MouseEvent e) {
                return SwingUtilities.isRightMouseButton(e) && (e.getModifiers() & 8) == 0;
            }

            public boolean isForceMarqueeEvent(MouseEvent event) {
                return this.test(event);
            }

            public void mouseReleased(MouseEvent e) {
                if (this.test(e)) {
                    e.consume();
                } else {
                    super.mouseReleased(e);
                }

            }

            public void mousePressed(MouseEvent e) {
                if (this.test(e)) {
                    synchronized(ProMJGraphPanel.this.getGraph().getProMGraph()) {
                        Object cell = ProMJGraphPanel.this.getGraph().getFirstCellForLocation((double)e.getX(), (double)e.getY());
                        if (cell == null) {
                            ProMJGraphPanel.this.getGraph().clearSelection();
                            new ArrayList(0);
                        } else if (ProMJGraphPanel.this.getGraph().getSelectionModel().isCellSelected(cell)) {
                            Collection var4 = ProMJGraphPanel.this.getSelectedElements();
                        } else {
                            Collection<DirectedGraphElement> sel = new ArrayList(1);
                            sel.add(ProMJGraphPanel.this.getElementForLocation((double)e.getX(), (double)e.getY()));
                            ProMJGraphPanel.this.getGraph().setSelectionCell(cell);
                        }
                    }
                } else {
                    super.mousePressed(e);
                }

            }
        });
        super.initialize();
    }

    public ProMJGraph getGraph() {
        return (ProMJGraph)this.getComponent();
    }

    public Collection<DirectedGraphElement> getSelectedElements() {
        List<DirectedGraphElement> elements = new ArrayList();
        Object[] arr$ = this.getGraph().getSelectionCells();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            if (o instanceof ProMGraphCell) {
                elements.add(((ProMGraphCell)o).getNode());
            } else if (o instanceof ProMGraphEdge) {
                elements.add(((ProMGraphEdge)o).getEdge());
            }
        }

        return elements;
    }

    public Collection<DirectedGraphNode> getSelectedNodes() {
        List<DirectedGraphNode> nodes = new ArrayList();
        Object[] arr$ = this.getGraph().getSelectionCells();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            if (o instanceof ProMGraphCell) {
                nodes.add(((ProMGraphCell)o).getNode());
            }
        }

        return nodes;
    }

    public Collection<DirectedGraphEdge<?, ?>> getSelectedEdges() {
        List<DirectedGraphEdge<?, ?>> edges = new ArrayList();
        Object[] arr$ = this.getGraph().getSelectionCells();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object o = arr$[i$];
            if (o instanceof ProMGraphEdge) {
                edges.add(((ProMGraphEdge)o).getEdge());
            }
        }

        return edges;
    }

    public DirectedGraphElement getElementForLocation(double x, double y) {
        Object cell = this.getGraph().getFirstCellForLocation(x, y);
        if (cell instanceof ProMGraphCell) {
            return ((ProMGraphCell)cell).getNode();
        } else {
            return cell instanceof ProMGraphEdge ? ((ProMGraphEdge)cell).getEdge() : null;
        }
    }
}
