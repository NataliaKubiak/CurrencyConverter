package testStuff.testDAO;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        Config config = new Config();
//        ExchangeCurrencyDAO exchangeCurrencyDAO = new ExchangeCurrencyDAO(config.jdbcTemplate());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json;
//
//        try {
//            json = objectMapper.writeValueAsString(exchangeCurrencyDAO.getAllRates());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println(json);

        Map<String, String> rrr = new HashMap<>();

        rrr.put("Me", "12234");
        rrr.put("You", "45345");
        rrr.put("She", "888");
        rrr.put("He", "000");

        System.out.println(rrr.get("They"));
    }

}
