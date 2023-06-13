package org.project;

import java.awt.*;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.jgraph.ProcessTreeVisualisation;
import org.processmining.models.jgraph.VisualizeAcceptingPetriNetPlugin;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.inductiveminer2.InductiveMinerPlugin;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class WebController {

    final String defaultPath = "./src/main/resources/datasets/reviewing.xes";

    final int WIDTH = 1600;

    final int HEIGH = 1000;

    @GetMapping("/inductiveMiner/efficientTree")
    public ResponseEntity<Resource> inductiveMinerEfficientTree(@RequestParam(value = "path", defaultValue = defaultPath) String path) {
        XLog log = getXESFromPath(path);

        EfficientTree result = InductiveMinerPlugin.mineGuiProcessTree(log);

        FakePluginContext fakePluginContext = new FakePluginContext();
        JPanel jPanel = ProcessTreeVisualisation.fancy(fakePluginContext, result);

        BufferedImage image = getBufferedImageFromJComponent(jPanel);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/inductiveMiner/acceptingPetriNet")
    public ResponseEntity<Resource> inductiveMinerAcceptingPetrinet(@RequestParam(value = "path", defaultValue = defaultPath) String path) throws EfficientTreeReduce.ReductionFailedException {
        InductiveMinerPlugin inductiveMinerPlugin = new InductiveMinerPlugin();

        XLog log = getXESFromPath(path);

        AcceptingPetriNet result = inductiveMinerPlugin.mineGuiAcceptingPetriNet(log);

        FakePluginContext fakePluginContext = new FakePluginContext();
        JComponent jc = VisualizeAcceptingPetriNetPlugin.visualize(fakePluginContext, result);

        BufferedImage image = getBufferedImageFromJComponent(jc);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @GetMapping("/splitminer")
    public ResponseEntity<Resource> splitminer(@RequestParam(value = "path", defaultValue = defaultPath) String path) {

        XLog log = getXESFromPath(path);

        double eta = 0.4; //SplitMinerUIResult.FREQUENCY_THRESHOLD;	// 0.0 to 1.0
        double epsilon = 0.1; //SptlitMinerUIResult.PARALLELISMS_THRESHOLD;	// 0.0 to 1.0

        SplitMiner splitminer = new SplitMiner();
        BPMNDiagram output = splitminer.mineBPMNModel(log, eta, epsilon, DFGPUIResult.FilterType.FWG, true, true, false, SplitMinerUIResult.StructuringTime.NONE);
        Object[] result = BPMNToPetriNetConverter.convert(output);
        Petrinet generatedPetriNet = (Petrinet) result[0];
        System.out.println("PETRINET GENERATED SUCCESSFULLY");

        Marking m = new Marking();
        ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
        for (Place p : m) {
            String label = "" + m.occurrences(p);
            map.putViewSpecific(p, AttributeMap.LABEL, label);
            map.putViewSpecific(p, AttributeMap.TOOLTIP, p.getLabel());
            map.putViewSpecific(p, AttributeMap.SHOWLABEL, !label.equals(""));
        }
        FakePluginContext fakePluginContext = new FakePluginContext();
        JComponent jc = ProMJGraphVisualizer.instance().visualizeGraph(fakePluginContext, generatedPetriNet, map);

        BufferedImage image = getBufferedImageFromJComponent(jc);


        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(imageBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public XLog getXESFromPath(String path) {
        String xesPath = path;
        XesXmlParser xesParser = new XesXmlParser();
        XLog log = null;
        try {
            log = xesParser.parse(new File(xesPath)).get(0);
            System.out.println("Imported Event Log summary:");
            System.out.println("Number of traces: " + log.size());
            System.out.println("Number of events: " + log.stream().mapToInt(XTrace::size).sum());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return log;
    }

    public BufferedImage getBufferedImageFromJComponent(JComponent jComponent) {

        try {
            JFrame frame = new JFrame();
            frame.setSize(WIDTH, HEIGH);
            frame.add(jComponent);
            frame.setVisible(true);

            Thread.sleep(1000);

            BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics graphics = image.createGraphics();
            jComponent.paint(graphics);
            graphics.dispose();

            //Debug File...
            File outputfile = new File("./src/main/resources/images.png");
            ImageIO.write(image, "png", outputfile);

            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
