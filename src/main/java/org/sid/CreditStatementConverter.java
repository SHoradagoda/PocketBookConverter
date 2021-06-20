package org.sid;

import com.google.common.collect.Sets;
import com.google.common.io.PatternFilenameFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreditStatementConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditStatementConverter.class);
    private final QifReader qifReader = new QifReader();
    private final QifWriter qifWriter = new QifWriter();
    static final String CREDIT_STATEMENT_DIR = "D:\\Onedrive\\Documents\\Finances\\2021\\Bank Statements\\Credit";
    static final String OUTPUT_DIR = "C:\\Sid\\Temp\\CC";

    public static void main (String[] args) {
        CreditStatementConverter converter = new CreditStatementConverter();
        List<QifData> qifData = converter.readAllFiles(CREDIT_STATEMENT_DIR);
        LOGGER.info("Read {} files", qifData.size());
        converter.writeQifData(converter.mergedData(qifData));
        LOGGER.info("Wrote {} files", qifData.size());
    }

    public List<QifData> readAllFiles(String directory) {
        String[] files = getPdfFileNames(directory);
        return Stream.of(files).map(f -> fileToQifData(directory, f)).collect(Collectors.toList());
    }

    public QifData mergedData( List<QifData> qifDataList ) {
        final QifData qifData = new QifData("AllTrans.qif");
        Set<QifEntry> uniqueEntries = Sets.newHashSet();
        qifDataList.forEach(q -> q.getQifEntries().forEach(qq->uniqueEntries.add(qq)));
        qifData.getQifEntries().addAll(uniqueEntries);
        Collections.sort(qifData.getQifEntries(), Comparator.comparing(QifEntry::getDate));
        Collections.reverse(qifData.getQifEntries());
        return qifData;
    }

    String[] getPdfFileNames(String dir) {
        return Paths.get(dir).toFile().list(new PatternFilenameFilter(".*pdf$"));
    }

    private QifData fileToQifData(String dir,String fileName) {
        final QifData qifData = new QifData(fileName.replace(".pdf", ".qif"));
        try {
            PDDocument document = PDDocument.load(new File( dir, fileName));
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                Stream.of(text.split("\n"))
                        .map(l -> qifReader.toQifEntry(l))
                        .filter(Objects::nonNull)
                        .forEach(q -> qifData.addQifEntry(q));
                document.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qifData;
    }

    private void writeQifData(List<QifData> qifDataList) {
        qifDataList.forEach(qifData -> qifWriter.writeToQifFile(qifData, OUTPUT_DIR));
    }
    private void writeQifData(QifData qifData) {
        qifWriter.writeToQifFile(qifData, OUTPUT_DIR);
    }
}
