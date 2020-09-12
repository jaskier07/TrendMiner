package pl.kania.trendminer.dataparser.parser.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnglishSpeakingCountryDetector {

    private final CountryDetector countryDetector;
    private static final Set<String> ENGLISH_SPEAKING_COUNTRIES = getEnglishSpeakingCountries();

    public EnglishSpeakingCountryDetector(@Autowired Environment environment) {
         countryDetector = new CountryDetector(environment.getProperty("pl.kania.path.places"));
    }

    public boolean isInEnglishSpeakingCountry(String location) {
        Optional<String> country = countryDetector.detect(location);
        if (country.isEmpty()) {
            return false;
        }
        return ENGLISH_SPEAKING_COUNTRIES.contains(country.get());
    }

    private static Set<String> getEnglishSpeakingCountries() {
        return Set.of("Antigua and Barbuda",
                "Australia",
                "The Bahamas",
                "Barbados",
                "Belize",
                "Canada",
                "Dominica",
                "Grenada",
                "Guyana",
                "Ireland",
                "Jamaica",
                "New Zealand",
                "St Kitts and Nevis",
                "St Lucia",
                "St Vincent and the Grenadines",
                "Trinidad and Tobago",
                "United Kingdom",
                "United States",
                "USA")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
