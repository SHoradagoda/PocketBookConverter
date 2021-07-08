package org.sid;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QifStatementReader {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QifData fileToQifData(String dir,String fileName) {
        QifData qifData = new QifData(fileName);
        try {
            List<String> lines = Files.readAllLines(new File(dir, fileName).toPath(), Charset.defaultCharset());
            QifEntry qifEntry = new QifEntry();
            for ( String line : lines) {
                if ( line.startsWith("D")) {
                    qifEntry = new QifEntry();
                    qifEntry.setDate(toDate(line.substring(1, 11)));
                }
                if (line.startsWith("T")) {
                    qifEntry.setAmount(new BigDecimal(line.substring(1)));
                }
                if (line.startsWith("P")){
                    qifEntry.setMerchant(line.substring(1).trim());
                }
                if ( line.startsWith("^")) {
                    qifData.addQifEntry(qifEntry);
                }
            }

        } catch ( Exception e ){
            throw new RuntimeException(e);
        }
        return qifData;
    }

    private LocalDate toDate(String entry) {
        try {
            return LocalDate.parse(entry, DATE_TIME_FORMATTER);
        }catch (Exception  e ){
            return null;
        }
    }
}
