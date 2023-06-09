package org.processmining.plugins.splitminer;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import org.apache.commons.collections15.Transformer;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class MinePetrinetWithSplitMiner {

	 public static BufferedImage visualizePetrinet(Petrinet petriNet) {
		    Graph<String, String> graph = new DirectedSparseGraph<>();

		    // Add places as nodes
		    for (Place place : petriNet.getPlaces()) {
		        graph.addVertex(place.getLabel());
		    }

		    // Add transitions as nodes
		    for (Transition transition : petriNet.getTransitions()) {
		        graph.addVertex(transition.getLabel());
		    }

		    // Add arcs as edges
		    for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc : petriNet.getEdges()) {
		        String source = arc.getSource().getLabel();
		        String target = arc.getTarget().getLabel();
		        graph.addEdge(source + "-" + target, source, target);
		    }

		    // Create visualization components
		    Layout<String, String> layout = new FRLayout<>(graph);
		    layout.setSize(new Dimension(950, 750));
		    VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1100, 900));
		    vv.setBackground(Color.WHITE); // Set the background color to white

		    // Set the vertex label transformer to display the labels inside the nodes
		    vv.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>() {
		        @Override
		        public String transform(String v) {
		            return "<html><center>" + v + "</center></html>";
		        }
		    });

		    // Set the vertex shape transformer to display transition nodes as black rectangles and place nodes as white rectangles
		    vv.getRenderContext().setVertexShapeTransformer(v -> {
		        if (petriNet.getTransitions().stream().anyMatch(t -> t.getLabel().equals(v))) {
		            return new Rectangle(-20, -20, 40, 40);
		        } else {
		            return new Rectangle(-20, -20, 40, 40);
		        }
		    });

		    // Set the vertex label font and color
		    vv.getRenderContext().setVertexFontTransformer(v -> new Font("Arial", Font.PLAIN, 12));
		    vv.getRenderContext().setVertexFillPaintTransformer(v -> {
		        if (petriNet.getTransitions().stream().anyMatch(t -> t.getLabel().equals(v))) {
		            return Color.BLACK;
		        } else {
		            return Color.WHITE;
		        }
		    });

		    // Create an image of the graph
		    BufferedImage image = (BufferedImage) vv.getImage(new Point(-400, -200), vv.getGraphLayout().getSize());

		    return image;
		}




	 
	 

}

