package org.processmining.plugins.splitminer;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@Plugin(name = "Mine Petri Net with Split Miner", parameterLabels = { "Log" }, returnLabels = { "Mined Model" }, returnTypes = {
		Petrinet.class }, userAccessible = true, categories = { PluginCategory.Discovery })


public class SplitMinerPlugin {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "N. Tax", email = "n.tax@tue.nl", pack = "LocalProcessModelConformance")
	@PluginVariant(variantLabel = "Mine Petri Net with Split Miner", requiredParameterLabels = { 0 })
	public static Petrinet minePetrinetWithSplitMiner(PluginContext context, XLog log){
	        double eta = 0.4;//SplitMinerUIResult.FREQUENCY_THRESHOLD;	// 0.0 to 1.0
	        double epsilon = 0.1;//SplitMinerUIResult.PARALLELISMS_THRESHOLD;	// 0.0 to 1.0
			
			SplitMiner splitminer = new SplitMiner();
	        BPMNDiagram output = splitminer.mineBPMNModel(log, eta, epsilon, DFGPUIResult.FilterType.FWG, true, true, false, SplitMinerUIResult.StructuringTime.NONE);
			
			Object[] result = BPMNToPetriNetConverter.convert(output);
			return (Petrinet) result[0];
	}


}

