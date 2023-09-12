package restJpa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManagerDTO implements Serializable{
    
    private int id;
    private String name;
    private Double revenue;
    private List<ClientDTO> myClients;

    public ManagerDTO(){}

    public ManagerDTO(int id, String name, Double revenue){
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

    public List<ClientDTO> getMyClients() {
        return myClients;
    }

    public void setMyClients(List<ClientDTO> myClients) {
        this.myClients = myClients;
    }
}
