package org.processmining.plugins.inductiveminer2;

import javax.swing.JOptionPane;

//import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import org.apache.commons.collections15.Transformer;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParametersForPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.InductiveMinerDefaultParameter;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InductiveMinerPlugin {
	
	
	@Plugin(name = "Mine efficient tree with Inductive Miner", level = PluginLevel.Regular, returnLabels = {
			"Efficient Tree" }, returnTypes = {
					EfficientTree.class }, parameterLabels = { "Log" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a Process Tree, dialog", requiredParameterLabels = { 0 })
	public EfficientTree mineGuiProcessTree(XLog xLog) {
		
		/* Get variables
		InductiveMinerDialog dialog = new InductiveMinerDialog(xLog);
		InteractionResult result = context.showWizard("Mine using Inductive Miner", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}


		//check that the log is not too big and mining might take a long time
		if (!confirmLargeLogs(context, log, dialog)) {
			context.getFutureResult(0).cancel(false);
			return null;
		}
		*/

		MiningParameters parameters = new InductiveMinerDefaultParameter();
		System.out.println(parameters.getClassifier().toString());
		System.out.println(parameters.getNoiseThreshold());
		System.out.println(parameters.isDebug());
		System.out.println(parameters.isUseMultithreading());
		IMLog log = parameters.getIMLog(xLog);

		return InductiveMiner.mineEfficientTree(log, parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
	}


	@Plugin(name = "Mine accepting Petri net with Inductive Miner - directly follows", level = PluginLevel.Regular, returnLabels = {
			"Accepting Petri net" }, returnTypes = { AcceptingPetriNet.class }, parameterLabels = {
			"Directly follows graph + minimum self-distance graph" }, userAccessible = true)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email, uiHelp = "Running this plug-in equals running:<br>1) \"Mine efficient tree with Inductive Miner\", <br>2) \"Reduce efficient tree language-equivalently for size\"<br>3) \"Convert efficient tree to Accepting Petri Net and reduce\" ")
	public AcceptingPetriNet mineGuiAcceptingPetriNet(XLog xLog)
			throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineGuiProcessTree(xLog);
		return postProcessTree2PetriNet(tree, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
	}

	public static EfficientTree mineTree(IMLog log, MiningParameters parameters, Canceller canceller) {
		return InductiveMiner.mineEfficientTree(log, parameters, canceller);
	}

	public static AcceptingPetriNet minePetriNet(IMLog log, MiningParameters parameters, Canceller canceller)
			throws UnknownTreeNodeException, ReductionFailedException {
		EfficientTree tree = mineTree(log, parameters, canceller);
		return postProcessTree2PetriNet(tree, canceller);
	}

	public static AcceptingPetriNet postProcessTree2PetriNet(EfficientTree tree, Canceller canceller)
			throws UnknownTreeNodeException, ReductionFailedException {
		if (tree == null || canceller.isCancelled()) {
			return null;
		}

		EfficientTreeReduce.reduce(tree, new EfficientTreeReduceParametersForPetriNet(false));

		AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);

		if (net == null || canceller.isCancelled()) {
			return null;
		}

		ReduceAcceptingPetriNetKeepLanguage.reduce(net, canceller);
		return net;
	}

	public static boolean confirmLargeLogs(final UIPluginContext context, IMLog log/* ,InductiveMinerDialog dialog*/) {
		//if (dialog.getVariant().getWarningThreshold() > 0) {
			int numberOfActivities = log.getNumberOfActivities();
			//if (numberOfActivities > dialog.getVariant().getWarningThreshold()) {
			//	int cResult = JOptionPane.showConfirmDialog(null,
			//			dialog.getVariant().toString() + " might take a long time, as the event log contains "
			//					+ numberOfActivities
			//					+ " activities.\nThe chosen variant of Inductive Miner is exponential in the number of activities.\nAre you sure you want to continue?",
			//			"Inductive Miner might take a while", JOptionPane.YES_NO_OPTION);

			//	return cResult == JOptionPane.YES_OPTION;
			//}
		//}
		return true;
	}
/*
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

 */

}
