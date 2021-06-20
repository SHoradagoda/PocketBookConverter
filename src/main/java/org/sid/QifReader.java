package org.sid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QifReader {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public QifEntry toQifEntry(String line) {
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
