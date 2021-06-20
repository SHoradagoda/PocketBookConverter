package org.sid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class QifEntry {

    @Getter @Setter private LocalDate date;
    @Getter @Setter private BigDecimal amount;
    @Getter @Setter private String merchant;


}

