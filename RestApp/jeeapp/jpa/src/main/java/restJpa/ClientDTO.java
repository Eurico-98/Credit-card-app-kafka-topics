package restJpa;
import java.io.Serializable;

public class ClientDTO implements Serializable{
   
    private int id;
    private String name;
    private ManagerDTO myManager;
    private Double credits;
    private Double payments;
    private Double balance;
    private Double last_month_bill;
    private int payments_made_in_past_hour;

    public ClientDTO(){}

    public ClientDTO(int id, String name, ManagerDTO myManager, Double credits, Double payments, Double balance, Double last_month_bill, int payments_made_in_past_hour){
        this.id = id;
        this.name = name;
        this.myManager = myManager;
        this.credits = credits;
        this.payments = payments;
        this.balance = balance;
        this.last_month_bill = last_month_bill;
        this.payments_made_in_past_hour = payments_made_in_past_hour;
    }
    
    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public ManagerDTO getMyManager() {
        return myManager;
    }

    public void setMyManager(ManagerDTO myManager) {
        this.myManager = myManager;
    }

    public void setCredits(Double credits){
        this.credits = credits;
    }

    public Double getCredits(){
        return credits;
    }

    public void setPayments(Double payments){
        this.payments = payments;
    }

    public Double getPayments(){
        return payments;
    }

    public void setBalance(Double balance){
        this.balance = balance;
    }

    public Double getBalance(){
        return balance;
    }

    public void setLast_month_bill(Double last_month_bill){
        this.last_month_bill = last_month_bill;
    }

    public Double getLast_month_bill(){
        return last_month_bill;
    }

    public void setPayments_made_in_past_hour(int payments_made_in_past_hour){
        this.payments_made_in_past_hour = payments_made_in_past_hour;
    }

    public int getPayments_made_in_past_hour(){
        return payments_made_in_past_hour;
    }
}
