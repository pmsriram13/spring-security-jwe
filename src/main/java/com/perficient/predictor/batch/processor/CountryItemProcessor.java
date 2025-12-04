package com.perficient.predictor.batch.processor;

import com.perficient.predictor.batch.dto.CountryCsvInput;
import com.perficient.predictor.batch.dto.CountryDBOutput;
import com.perficient.predictor.batch.exception.ValidationException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processes a CountryCsvInput record, validates it, and converts it to a CountryDBOutput object.
 */
public class CountryItemProcessor implements ItemProcessor<CountryCsvInput, CountryDBOutput> {

    @Override
    public CountryDBOutput process(CountryCsvInput item) throws ValidationException {

        if (item.countryName() == null || item.countryName().trim().isEmpty()) {
            throw new ValidationException("Country name is missing.");
        }
        if (item.countryId() == null || (item.countryId().trim().length() != 3 && !item.countryId().equals("999"))) {
            throw new ValidationException("Country Code is invalid or missing (must be 3 chars or '999'): " + item.countryId());
        }

        CountryDBOutput output = new CountryDBOutput(
                item.countryName().trim(),
                item.countryId().trim(),
                "COUNTRY_LOAD_JOB" // Static value for audit trail
        );
        return output;
    }
}