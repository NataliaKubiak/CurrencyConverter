package model.DTO;

public class ExchangeRateDTO {

    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private double rate;

    public ExchangeRateDTO(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
