package org.sid;

import com.google.common.collect.Sets;
import com.google.common.io.PatternFilenameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreditStatementConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditStatementConverter.class);
    private final CreditCardStatementReader bankStatementReader = new CreditCardStatementReader();
    private final QifStatementReader qifStatementReader = new QifStatementReader();
    private final QifWriter qifWriter = new QifWriter();
    static final String CREDIT_STATEMENT_DIR = "D:\\Onedrive\\Documents\\Finances\\2021\\Bank Statements\\Credit";
    static final String OUTPUT_DIR = "C:\\Sid\\Temp\\CC";

    public static void main(String[] args) {
        new CreditStatementConverter().convert(CREDIT_STATEMENT_DIR);
    }

    public void convert(String directory) {
        List<QifData> qifDataFromPdf = readPdfFiles(directory);
        List<QifData> qifDataQif = readQifFiles(directory);
        QifData mergedQifData = mergedData(qifDataFromPdf, qifDataQif);
        qifWriter.writeToQifFile(mergedQifData, "AllTransactions.qif", OUTPUT_DIR);
    }

    List<QifData> readPdfFiles(String directory) {
        String[] pdfFileNames = Paths.get(directory).toFile().list(new PatternFilenameFilter(".*pdf$"));
        List<QifData> qifDataList = Stream.of(pdfFileNames)
                .map(f -> bankStatementReader.fileToQifData(directory, f))
                .collect(Collectors.toList());
        LOGGER.info("Read {} PDF files from {}", qifDataList.size(), directory);
        return qifDataList;
    }

    List<QifData> readQifFiles(String directory) {
        String[] qifFiles = Paths.get(directory).toFile().list(new PatternFilenameFilter(".*qif$"));
        return Stream.of(qifFiles)
                .map(f -> qifStatementReader.fileToQifData(directory, f))
                .collect(Collectors.toList());
    }

    QifData mergedData(List<QifData> fromPdf, List<QifData> fromQif) {
        final QifData qifData = new QifData("AllTrans.qif");
        fromQif.forEach(qd ->qifData.getQifEntries().addAll(qd.getQifEntries())); //Qif data will be added
        LocalDate minDate = getMinDate(qifData);
        fromPdf.forEach(qd -> qd.getQifEntries().forEach( entry -> {
                if ( entry.getDate().isBefore(minDate)) {
                    qifData.getQifEntries().add(entry);
                }
        }));
        Collections.sort(qifData.getQifEntries(), Comparator.comparing(QifEntry::getDate));
        return qifData;
    }

    private LocalDate getMinDate(QifData qifData) {
        LocalDate minDate = LocalDate.now();
        for ( QifEntry e : qifData.getQifEntries()) {
            if ( minDate.isAfter(e.getDate()))
                minDate = e.getDate();
        }
        return minDate;
    }

}
