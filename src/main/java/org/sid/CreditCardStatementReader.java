package org.sid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

public class CreditCardStatementReader {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QifData fileToQifData(String dir,String fileName) {
        final QifData qifData = new QifData(fileName.replace(".pdf", ".qif"));
        try {
            PDDocument document = PDDocument.load(new File( dir, fileName));
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                Stream.of(text.split("\n"))
                        .map(l -> toQifEntry(l))
                        .filter(Objects::nonNull)
                        .forEach(q -> qifData.addQifEntry(q));
                document.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qifData;
    }

    private QifEntry toQifEntry(String line) {
        LocalDate date = readTransactionDate(line);
        if ( date == null || line.contains("OPENING BALANCE") || line.contains("CLOSING BALANCE")) {
            return null;
        }
        QifEntry entry = new QifEntry();
        entry.setDate(date);
        entry.setAmount(readAmount(line));
        entry.setMerchant(readMerchant(line));
        return entry;
    }

    private String readMerchant(String line) {
        int start = line.indexOf(" ", 12);
        int end = line.lastIndexOf("$");
        return line.substring(start, end);
    }

    private BigDecimal readAmount(String line) {
        return BigDecimal.valueOf(Double.parseDouble(line.split("\\$")[1]
                .split("\r")[0].replaceAll(",","")));
    }

    private LocalDate readTransactionDate(String line) {
        LocalDate date = null;
        String[] split = line.split(" ");
        if ( split.length >0 ) {
            date = toDate(split[0]);
            if ( date == null) {
                date = toDate("0" +split[0]);
            }
        }
        return date;
    }


    private LocalDate toDate(String entry) {
        try {
            return LocalDate.parse(entry, DATE_TIME_FORMATTER);
        }catch (Exception  e ){
            return null;
        }
    }

}
