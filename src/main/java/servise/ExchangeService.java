package servise;

import exceptions.BusinessLogicException;
import exceptions.NoDataFoundException;
import model.DTO.ExchangeRateDTO;
import model.DTO.MoneyExchangeDTO;
import model.ExchangeRate;
import model.MoneyExchange;
import repository.ExchangeDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExchangeService extends BaseService {

    private ExchangeDAO exchangeDAO = new ExchangeDAO(connectionUtil.jdbcTemplate());

    public List<ExchangeRate> getAll() {
        return exchangeDAO.getAllRates();
    }

    public ExchangeRate getExchangeRate(String baseCode, String targetCode) {
        return exchangeDAO.getRateByCurrencyCodes(baseCode, targetCode);
    }

    public ExchangeRate patchExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        isCurrencyCodeMatchesPattern(baseCurrencyCode);
        isCurrencyCodeMatchesPattern(targetCurrencyCode);

        exchangeDAO.updateExchangeRate(
                exchangeRateDTO.getRate(),
                baseCurrencyCode,
                targetCurrencyCode
        );

        return exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRate addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        isCurrencyCodeMatchesPattern(baseCurrencyCode);
        isCurrencyCodeMatchesPattern(targetCurrencyCode);

        exchangeDAO.create(exchangeRateDTO);

        return getExchangeRate(baseCurrencyCode, targetCurrencyCode);
    }

    public MoneyExchange exchangeMoney(MoneyExchangeDTO moneyExchangeDTO) {
        String baseCurrencyCode = moneyExchangeDTO.getBaseCurrencyCode();
        String targetCurrencyCode = moneyExchangeDTO.getTargetCurrencyCode();
        double amount = moneyExchangeDTO.getAmount();

        //проверяем валидность кодов валют
        isCurrencyCodeMatchesPattern(baseCurrencyCode);
        isCurrencyCodeMatchesPattern(targetCurrencyCode);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new BusinessLogicException("Base currency and Target Currency are the same currencies");
        }

        try {
            ExchangeRate ABrate = exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
            //тут пишем логику если нашлась валютная пара AB - берём её курс
            BigDecimal bdRate = new BigDecimal(Double.toString(ABrate.getRate()));

            BigDecimal convertedAmount = calcConvertedAmount(bdRate, amount);

            return new MoneyExchange(
                    ABrate.getBaseCurrency(),
                    ABrate.getTargetCurrency(),
                    ABrate.getRate(),
                    amount,
                    convertedAmount);

        } catch (NoDataFoundException ex) {
            try {
                ExchangeRate BArate = exchangeDAO.getRateByCurrencyCodes(targetCurrencyCode, baseCurrencyCode);
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

            } catch (NoDataFoundException e) {
                ExchangeRate usdToArate = exchangeDAO.getRateByCurrencyCodes("USD", baseCurrencyCode);
                ExchangeRate usdToBrate = exchangeDAO.getRateByCurrencyCodes("USD", targetCurrencyCode);
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
                        convertedAmount
                );
            }

        }
        // Если до сюда дошло выполнение, то ни одна ветка не сработала
        // и завершится с NoDataFoundException из последнего блока
    }

    private BigDecimal calcConvertedAmount(BigDecimal rate, double amount) {
        BigDecimal bdAmount = new BigDecimal(Double.toString(amount));

        BigDecimal convertedAmount = bdAmount.multiply(rate);

        return convertedAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
