package restJpa;
import java.io.Serializable;

public class ResultsDTO implements Serializable{
    
    private String id;
    private Double total_credits;
    private Double total_payments;
    private Double total_balance;
    private int client_w_biggest_debt;
    private int manager_w_biggest_revenue;

    public ResultsDTO(){}

    public ResultsDTO(Double total_credits, Double total_payments, Double total_balance, int client_w_biggest_debt, int manager_w_biggest_revenue){
        this.id = "AllClients";
        this.total_credits = total_credits;
        this.total_payments = total_payments;
        this.total_balance = total_balance;
        this.client_w_biggest_debt = client_w_biggest_debt;
        this.manager_w_biggest_revenue = manager_w_biggest_revenue;
    }

    public String getId(){
        return id;
    }

    public void setTotal_credits(Double total_credits){
        this.total_credits = total_credits;
    }

    public Double getTotal_credits(){
        return total_credits;
    }

    public void setTotal_payments(Double total_payments){
        this.total_payments = total_payments;
    }

    public Double getTotal_payments(){
        return total_payments;
    }

    public void setTotal_balance(Double total_balance){
        this.total_balance = total_balance;
    }

    public Double getTotal_balance(){
        return total_balance;
    }

    public void setClient_w_biggest_debt(int client_w_biggest_debt){
        this.client_w_biggest_debt = client_w_biggest_debt;
    }

    public int getClient_w_biggest_debt(){
        return client_w_biggest_debt;
    }

    public void setManager_w_biggest_revenue(int manager_w_biggest_revenue){
        this.manager_w_biggest_revenue = manager_w_biggest_revenue;
    }

    public int getManager_w_biggest_revenue(){
        return manager_w_biggest_revenue;
    }
}
