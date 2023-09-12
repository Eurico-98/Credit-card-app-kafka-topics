package restJpa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Manager implements Serializable{
    
    @Id
    private int id;
    private String name;
    private Double revenue;
    @OneToMany(mappedBy = "myManager", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Client> myClients;

    public Manager(){}

    public Manager(int id, String name, Double revenue){
        this.id = id;
        this.name = name;
        this.revenue = revenue;
        this.myClients = new ArrayList<>();
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

    public void setRevenue(Double revenue){
        this.revenue = revenue;
    }

    public Double getRevenue(){
        return revenue;
    }

    public List<Client> getMyClients() {
        return myClients;
    }

    public void setMyClients(List<Client> myClients) {
        this.myClients = myClients;
    }
}
