package pl.kania.trendminer.dataparser.preproc.replacing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MentionReplacerTest {

    @CsvSource({
            "RT @Keemokazi1: I showed my mom how to get Corona virus and this is what happened \uD83D\uDE02\uD83D\uDE02 https://t.co/STTyxnj8xw,I showed my mom how to get Corona virus and this is what happened \uD83D\uDE02\uD83D\uDE02 https://t.co/STTyxnj8xw",
            "RT @IngrahamAngle: California doctors say they've seen more deaths from suicide than coronavirus since lockdowns,California doctors say they've seen more deaths from suicide than coronavirus since lockdowns",
            "RT @true_pundit: CDC updates guidance to say COVID-19 ‘does not spread easily’ through touching contaminated surfaces,CDC updates guidance to say COVID-19 ‘does not spread easily’ through touching contaminated surfaces",
            "RT @parthpunter: Sudhir Chaudhary told them: “I don’t want to listen to any more complaints about someone’s fever or cough from tomorrow. R…,Sudhir Chaudhary told them: “I don’t want to listen to any more complaints about someone’s fever or cough from tomorrow. R…",
            "RT @nathanTbernard: Ben Shapiro falls short in desperate attempt at coronavirus comedy bit https://t.co/MQA4NBI5ia,Ben Shapiro falls short in desperate attempt at coronavirus comedy bit https://t.co/MQA4NBI5ia"
    })
    @ParameterizedTest
    void givenTweetContentRemoveResponseInformation(String text, String expected) {
        String replacedText = MentionReplacer.removeMentions(text).trim();
        Assertions.assertEquals(expected, replacedText);
    }
}