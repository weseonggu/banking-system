package com.msa.banking.personal.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.msa.banking.personal.domain.enums.BudgetPeriod;

import java.io.IOException;

public class BudgetPeriodDeserializer extends JsonDeserializer<BudgetPeriod> {

    @Override
    public BudgetPeriod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getText().toUpperCase();
        try {
            return BudgetPeriod.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("WEEKLY, MONTHLY 둘 중 기간을 선택해주세요. 기입된 값: " + value);
        }
    }
}
