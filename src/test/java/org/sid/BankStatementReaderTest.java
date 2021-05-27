package org.sid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankStatementReaderTest {

    @Test
    public void read() {
        new BankStatementReader().readAllFiles();
    }
}