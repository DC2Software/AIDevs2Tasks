package com.soprasteria.ai.devs.api.model.countryapi;

import java.util.List;

// API response contains more data - include more properties if necessary
public record CountryDetails(String name, String capital, String region, Integer population, Integer area, List<String> timezones) { }
