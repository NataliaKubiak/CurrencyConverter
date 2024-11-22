package servise;

import exceptions.BusinessLogicException;
import utils.Config;

public class BaseService {

    protected Config config = new Config();

    protected void isCurrencyCodeMatchesPattern(String code) {
        String codeRegex = "[A-Z]{3}";

        if (!code.matches(codeRegex)) {
            throw new BusinessLogicException("Invalid currency code.");
        }
    }
}
