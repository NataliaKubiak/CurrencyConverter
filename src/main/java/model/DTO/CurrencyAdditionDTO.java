package model.DTO;

public class CurrencyAdditionDTO {

    private String code;
    private String name;
    private String sign;

    public CurrencyAdditionDTO(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CurrencyAdditionDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
