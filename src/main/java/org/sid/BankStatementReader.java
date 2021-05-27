package org.sid;

import com.google.common.io.PatternFilenameFilter;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankStatementReader {
    private Logger LOGGER = Logger.getLogger(BankStatementReader.class);
    private static final String BANK_STATEMENT_DIR = "D:\\Onedrive\\Documents\\Finances\\2021\\Bank Statements";

    public List<QifData> readAllFiles() {
        String[] files = getPdfFileNames();
        return Stream.of(files).map(f -> toQifData(f)).collect(Collectors.toList());
    }

    String[] getPdfFileNames() {
        return Paths.get(BANK_STATEMENT_DIR).toFile().list(new PatternFilenameFilter(".*pdf$"));
    }

    private QifData toQifData(String fileName) {

        try {
            PDDocument document = PDDocument.load(new File(BANK_STATEMENT_DIR, fileName));
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                System.out.println("Text:" + text);
            }
            document.close();
            System.exit(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new QifData();
    }
}
