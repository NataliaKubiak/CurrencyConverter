package servise;

import exceptions.BusinessLogicException;
import utils.ConnectionUtil;

public class BaseService {

    protected ConnectionUtil connectionUtil = new ConnectionUtil();

    protected void isCurrencyCodeMatchesPattern(String code) {
        String codeRegex = "[A-Z]{3}";

        if (!code.matches(codeRegex)) {
            throw new BusinessLogicException("Invalid currency code.");
        }
    }
}
