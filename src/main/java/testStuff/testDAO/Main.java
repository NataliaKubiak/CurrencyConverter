package testStuff.testDAO;

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

        String num = "0.0002";
        String num2 = "0,0002";

        try {
            double rate = Double.parseDouble(num);
            System.out.println("НЕ упали в NumberFormatException   " + rate);

        } catch (NumberFormatException exception) {
            System.out.println("упали в NumberFormatException");
        }
    }

}
