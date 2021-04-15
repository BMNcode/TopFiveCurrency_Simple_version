import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CurrencyRunner {

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new URL("https://www.cbr-xml-daily.ru/daily_json.js"));
            JsonNode nameNode = rootNode.path("Valute");
            topFiveChangeCurrency(nameNode, mapper).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*
        the method runs through the JSON nodes,
        converts them to currency objects,
        sorts them by the largest difference in the exchange rate for the last day,
        and stores the 5 largest differences
     */
    static List<Currency> topFiveChangeCurrency(JsonNode jsonNode, ObjectMapper mapper) {
        return StreamSupport
                .stream(Spliterators
                        .spliteratorUnknownSize(jsonNode.fields(), Spliterator.ORDERED), false)
                .map(e -> {
                    try {
                        return mapper.treeToValue(e.getValue(), Currency.class);
                    } catch (JsonProcessingException jsonProcessingException) {
                        throw new RuntimeException(jsonProcessingException);
                    }
                })
                .sorted(Currency::compare)
                .limit(5)
                .collect(Collectors.toList());
    }

}
