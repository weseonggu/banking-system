package com.msa.banking.personal.domain.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msa.banking.personal.config.BudgetPeriodDeserializer;

@JsonDeserialize(using = BudgetPeriodDeserializer.class)
public enum BudgetPeriod {
    WEEKLY, MONTHLY
}
