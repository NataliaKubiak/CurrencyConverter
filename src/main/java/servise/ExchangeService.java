package servise;

import exceptions.InvalidInputParameterException;
import exceptions.NoDataFoundException;
import model.DTO.ExchangeRateDTO;
import model.DTO.MoneyExchangeDTO;
import model.ExchangeRate;
import model.MoneyExchange;
import repository.ExchangeDAO;
import validator.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeService extends BaseService {

    private ExchangeDAO exchangeDAO = new ExchangeDAO(connectionUtil.jdbcTemplate());

    public List<ExchangeRate> getAll() {
        return exchangeDAO.findAll();
    }

    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {

        return exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NoDataFoundException("No exchange rate found for given currency codes."));
    }

    public ExchangeRate changeExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        Validator.validateCurrencyCodeMatchesPattern(baseCurrencyCode);
        Validator.validateCurrencyCodeMatchesPattern(targetCurrencyCode);

        ExchangeRate rate = exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new NoDataFoundException("No exchange rate found for given currency codes: " + baseCurrencyCode + ", " + targetCurrencyCode));

        exchangeDAO.update(
                exchangeRateDTO.getRate(),
                baseCurrencyCode,
                targetCurrencyCode
        );

        rate.setRate(exchangeRateDTO.getRate());

        return rate;
    }

    public ExchangeRate addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        Validator.validateCurrencyCodeMatchesPattern(baseCurrencyCode);
        Validator.validateCurrencyCodeMatchesPattern(targetCurrencyCode);

        return exchangeDAO.create(baseCurrencyCode, targetCurrencyCode, exchangeRateDTO.getRate())
                .orElseThrow(() -> new NoDataFoundException("No currency codes: " + baseCurrencyCode + ", " + targetCurrencyCode + " were found in Database"));
    }

    public MoneyExchange exchangeMoney(MoneyExchangeDTO moneyExchangeDTO) {
        String baseCurrencyCode = moneyExchangeDTO.getBaseCurrencyCode();
        String targetCurrencyCode = moneyExchangeDTO.getTargetCurrencyCode();
        double amount = moneyExchangeDTO.getAmount();

        //проверяем валидность кодов валют
        Validator.validateCurrencyCodeMatchesPattern(baseCurrencyCode);
        Validator.validateCurrencyCodeMatchesPattern(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new InvalidInputParameterException("Base currency and Target Currency are the same currencies");
        }

            Optional<ExchangeRate> optionalABrate = exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);

            if (optionalABrate.isPresent()) {
                ExchangeRate ABrate = optionalABrate.get();

                //тут пишем логику если нашлась валютная пара AB - берём её курс
                BigDecimal bdRate = new BigDecimal(Double.toString(ABrate.getRate()));

                BigDecimal convertedAmount = calcConvertedAmount(bdRate, amount);

                return new MoneyExchange(
                        ABrate.getBaseCurrency(),
                        ABrate.getTargetCurrency(),
                        ABrate.getRate(),
                        amount,
                        convertedAmount);
            }

            Optional<ExchangeRate> optionalBArate = exchangeDAO.getRateByCurrencyCodes(targetCurrencyCode, baseCurrencyCode);

            if (optionalBArate.isPresent()) {
                ExchangeRate BArate = optionalBArate.get();
                //тут пишем логику если нашлась валютная пара BA (считаем обратный курс)

                BigDecimal rate = new BigDecimal(Double.toString(BArate.getRate()));

                //формула обратного курса: 1 / BArate
                BigDecimal ABrate = BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP);
                BigDecimal convertedAmount = calcConvertedAmount(ABrate, amount);

                return new MoneyExchange(
                        BArate.getTargetCurrency(),
                        BArate.getBaseCurrency(),
                        ABrate.doubleValue(),
                        amount,
                        convertedAmount);
            }

            Optional<ExchangeRate> optionalExchangeUsdToArate = exchangeDAO.getRateByCurrencyCodes("USD", baseCurrencyCode);
            Optional<ExchangeRate> optionalExchangeUsdToBrate = exchangeDAO.getRateByCurrencyCodes("USD", targetCurrencyCode);

            if (optionalExchangeUsdToBrate.isPresent() && optionalExchangeUsdToArate.isPresent()) {
                ExchangeRate usdToArate = optionalExchangeUsdToArate.get();
                ExchangeRate usdToBrate = optionalExchangeUsdToBrate.get();
                //тут пишем логику перевода через доллар

                BigDecimal rateUSDToB = new BigDecimal(Double.toString(usdToBrate.getRate()));
                BigDecimal rateUSDToA = new BigDecimal(Double.toString(usdToArate.getRate()));

                // Формула курса А к В через курс к $: rateUSDToB / rateUSDToA
                BigDecimal rateAtoB = rateUSDToB.divide(rateUSDToA, 6, RoundingMode.HALF_UP);

                BigDecimal convertedAmount = calcConvertedAmount(rateAtoB, amount);

                return new MoneyExchange(
                        usdToArate.getTargetCurrency(),
                        usdToBrate.getTargetCurrency(),
                        rateAtoB.doubleValue(),
                        amount,
                        convertedAmount);
            }

            throw new NoDataFoundException("No Rates for Currencies or Currencies found with Codes: " + baseCurrencyCode + ", " + targetCurrencyCode);
    }

    private BigDecimal calcConvertedAmount(BigDecimal rate, double amount) {
        BigDecimal bdAmount = new BigDecimal(Double.toString(amount));

        BigDecimal convertedAmount = bdAmount.multiply(rate);

        return convertedAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
