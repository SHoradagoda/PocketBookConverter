package org.sid;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QifWriter {

    private static final String HEADER = "!Type:Bank";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String QIF_ROW_FORMAT = "D%s\n" +
            "T%s\n" +
//            "P           TRANSFER            %S            Sankaragopalan Ramasamykone            565096337            IB1103933            INTERNET BANKING            \n" +
            "P           %s\n" +
            "^";

    public void writeToQifFile(QifData qifData, String outputPath) {
        List<String> qifStringList = qifDataToStringLis(qifData);
        try {
            Files.write(Paths.get(outputPath, qifData.getFileName()), qifStringList, Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<String> qifDataToStringLis(QifData qifData) {
        List<String> list = Lists.newArrayList(HEADER);
        qifData.getQifEntries().forEach(q -> list.add(qifToString(q)));
        return list;
    }

    private String qifToString(QifEntry q) {
        return String.format(QIF_ROW_FORMAT,
                DATE_TIME_FORMATTER.format(q.getDate()),
                q.getAmount(),
                q.getMerchant()
        );
    }
}
