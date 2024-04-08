package com.soprasteria.ai.devs.api.model.currencyapi;

import java.time.LocalDate;
import java.util.List;

public record ExchangeRateData(String currency, String code, List<Rate> rates) {

    public record Rate(String no, LocalDate effectiveDate, Double mid) {}
}

