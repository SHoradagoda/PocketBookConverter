package org.sid;

import com.google.common.collect.Lists;

import java.util.List;

public class QifData {

    private  final List<QifEntry> entries = Lists.newArrayList();
    private final String fileName;

    public QifData(String fileName) {
        this.fileName = fileName;
    }

    public void addQifEntry ( QifEntry entry ) {
        entries.add(entry);
    }

    public List<QifEntry> getQifEntries() {
        return entries;
    }

    public String getFileName() {
        return fileName;
    }
}
