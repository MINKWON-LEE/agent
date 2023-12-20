package com.mobigen.snet.NeAgent.main;

import org.apache.commons.exec.LogOutputStream;
import java.util.LinkedList;
import java.util.List;

public class CollectingLogOutputStream extends LogOutputStream {
    private final List lines = new LinkedList();

    protected void processLine(String line, int level) {
        lines.add(line);
    }

    public List getLines() {
        return lines;
    }
}