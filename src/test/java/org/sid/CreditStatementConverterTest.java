package org.sid;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreditStatementConverterTest {

    private CreditStatementConverter converter = new CreditStatementConverter();

    @Test
    public void readsPdf() {
        List<QifData> qifDataList = converter.readAllFiles(CreditStatementConverter.CREDIT_STATEMENT_DIR);
        assertNotNull(qifDataList);

    }
}