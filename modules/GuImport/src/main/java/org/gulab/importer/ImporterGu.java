/*
 */
package org.gulab.importer;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import static java.lang.reflect.Array.getLength;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportUtils;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author Mathieu Bastian, Sebastien Heymann
 */
public class ImporterGu implements FileImporter, LongTask {

    //Architecture
    private Reader reader;
    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;

    @Override
    public boolean execute(ContainerLoader container) {
        this.container = container;
        this.report = new Report();
        LineNumberReader lineReader = ImportUtils.getTextReader(reader);
        try {
            importData(lineReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                lineReader.close();
            } catch (IOException ex) {
            }
        }
        return !cancel;
    }

    private void importData(LineNumberReader reader) throws Exception {
        Progress.start(progressTicket);        //Progress

        List<String> lines = new ArrayList<String>();
        for (; reader.ready();) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                lines.add(line);
            }
        }

        Progress.switchToDeterminate(progressTicket, lines.size());

        int size = lines.size();

        for (int i = 0; i < size; i++) {
            if (cancel) {
                return;
            }
            String line = lines.get(i);
            String[] items = line.split("\t+");
            if (getLength(items) < 5) {
                throw new Exception(String.format("Incorrect column count in line %d.", i));
            }
            String sourceIp = items[0];
            String destIp = items[1];
            String freq = items[2];
            addNode(sourceIp, sourceIp);
            addNode(destIp, destIp);
            addEdge(sourceIp, destIp, Float.parseFloat(freq));
            Progress.progress(progressTicket);      //Progress
        }
    }

    private void addNode(String id, String label) {
        NodeDraft node;
        if (!container.nodeExists(id)) {
            node = container.factory().newNodeDraft(id);
            node.setLabel(label);
            container.addNode(node);
        }
    }

    private void addEdge(String source, String target) {
        addEdge(source, target, 1);
    }

    private void addEdge(String source, String target, float weight) {
        NodeDraft sourceNode;
        if (!container.nodeExists(source)) {
            sourceNode = container.factory().newNodeDraft(source);
            container.addNode(sourceNode);
        } else {
            sourceNode = container.getNode(source);
        }
        NodeDraft targetNode;
        if (!container.nodeExists(target)) {
            targetNode = container.factory().newNodeDraft(target);
            container.addNode(targetNode);
        } else {
            targetNode = container.getNode(target);
        }
        EdgeDraft edge = container.factory().newEdgeDraft();
        edge.setSource(sourceNode);
        edge.setTarget(targetNode);
        edge.setWeight(weight);
        container.addEdge(edge);
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
