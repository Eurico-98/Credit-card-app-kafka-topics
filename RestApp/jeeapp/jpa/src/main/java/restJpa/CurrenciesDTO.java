package restJpa;
import java.io.Serializable;

public class CurrenciesDTO implements Serializable{
    
    private String currency;
    private Double rate;

    public CurrenciesDTO(){}

    public CurrenciesDTO(String currency, Double rate){
        this.currency = currency;
        this.rate = rate;
    }
    
    public void setCurrency(String currency){
        this.currency = currency;
    }

    public String getCurrency(){
        return currency;
    }

    public void setRate(Double rate){
        this.rate = rate;
    }

    public Double getRate(){
        return rate;
    }
}
