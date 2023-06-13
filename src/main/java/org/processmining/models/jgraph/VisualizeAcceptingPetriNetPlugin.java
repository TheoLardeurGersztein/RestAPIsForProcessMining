package org.processmining.models.jgraph;

import java.awt.Color;
import java.util.Iterator;
import javax.swing.JComponent;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.semantics.petrinet.Marking;

@Plugin(
        name = "@0 Visualize Accepting Petri Net",
        level = PluginLevel.Regular,
        returnLabels = {"Visualized Accepting Petri Net"},
        returnTypes = {JComponent.class},
        parameterLabels = {"Accepting Petri Net"},
        userAccessible = true
)
@Visualizer
public class VisualizeAcceptingPetriNetPlugin {
    public VisualizeAcceptingPetriNetPlugin() {
    }

    @PluginVariant(
            requiredParameterLabels = {0}
    )
    public static JComponent visualize(UIPluginContext context, AcceptingPetriNet net) {
        return visualize((PluginContext)context, net);
    }

    public static JComponent visualize(PluginContext context, AcceptingPetriNet net) {
        ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
        Iterator var3 = net.getInitialMarking().baseSet().iterator();

        while(var3.hasNext()) {
            Place place = (Place)var3.next();
            map.putViewSpecific(place, "ProM_Vis_attr_fillcolor", new Color(128, 255, 0));
        }

        var3 = net.getFinalMarkings().iterator();

        while(var3.hasNext()) {
            Marking marking = (Marking)var3.next();
            Iterator var5 = marking.baseSet().iterator();

            while(var5.hasNext()) {
                Place place = (Place)var5.next();
                if (net.getInitialMarking().baseSet().contains(place)) {
                    map.putViewSpecific(place, "ProM_Vis_attr_fillcolor", new Color(128, 255, 0));
                    map.putViewSpecific(place, "ProM_Vis_attr_gradientcolor", new Color(228, 0, 0));
                } else {
                    map.putViewSpecific(place, "ProM_Vis_attr_fillcolor", new Color(228, 0, 0));
                }
            }
        }

        return ProMJGraphVisualizer.instance().visualizeGraph(context, net.getNet(), map);
    }
}
