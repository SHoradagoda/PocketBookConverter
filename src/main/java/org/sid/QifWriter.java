package org.sid;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QifWriter {

    private static final String HEADER = "!Type:Bank";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(QifWriter.class);

    private static final String QIF_ROW_FORMAT = "D%s\n" +
            "T%s\n" +
//            "P           TRANSFER            %S            Sankaragopalan Ramasamykone            565096337            IB1103933            INTERNET BANKING            \n" +
            "P           %s\n" +
            "^";
    private static final BigDecimal NEGATIVE_ONE = new BigDecimal(-1);

    public void writeToQifFile(QifData qifData, String outputFileName, String outputDirectory) {
        List<String> qifStringList = qifDataToStringLis(qifData);
        try {
            Path outputPath = Paths.get(outputDirectory, outputFileName);
            Files.write(outputPath, qifStringList, Charset.defaultCharset(), StandardOpenOption.CREATE);
            LOGGER.info("Wrote to {}", outputPath.toString());
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
                q.getAmount().multiply(NEGATIVE_ONE),
                q.getMerchant()
        );
    }
}
