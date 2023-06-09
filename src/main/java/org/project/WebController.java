package org.project;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.InductiveMinerPlugin;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.AttributedString;

@RestController
public class WebController {


    @GetMapping("/inductiveMiner/efficientTree")
    public String inductiveMiner(@RequestParam(value = "path", defaultValue = "running-example.xes") String path) {

        InductiveMinerPlugin inductiveMinerPlugin = new InductiveMinerPlugin();

        String path2 = "running-example.xes";
        String xesPath = path2;
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

        EfficientTree result = inductiveMinerPlugin.mineGuiProcessTree(log);
        System.out.println(result);

        File file = new File("EfficientTree");
        try {
            EfficientTreeExportPlugin.export(result, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

/*
        AcceptingPetriNet acceptingPetriNet = EfficientTree2AcceptingPetriNet.convert(result);

        CLIContext cliContext = new CLIContext();
        CLIPluginContext cliPluginContext = new CLIPluginContext(cliContext, "inductive miner");

        try {
            acceptingPetriNet.exportToFile(cliPluginContext, new File("Result.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /InductiveMinerPlugin.visualizePetrinet(acceptingPetriNet);
 */

        String content = null;
        Path filePath = Path.of("EfficientTree");
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Une page</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "        <p>" + content.replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;") + "</p>\n" +
                "  </body>\n" +
                "</html>";
    }

}
