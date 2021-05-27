package org.sid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankStatementReaderTest {

    private final BankStatementReader bankStatementReader = new BankStatementReader();

    @Test
    public void read() {
        bankStatementReader.readAllFiles();
    }

    @Test
    public void getPdfFileNames() {
        assertTrue ( bankStatementReader.getPdfFileNames().length > 0);
    }
}