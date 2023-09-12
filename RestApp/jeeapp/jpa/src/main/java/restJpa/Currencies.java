package restJpa;
import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Currencies implements Serializable{
    
    @Id
    private String currency;
    private Double rate;

    public Currencies(){}

    public Currencies(String currency, Double rate){
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
