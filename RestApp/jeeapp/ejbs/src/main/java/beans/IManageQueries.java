package beans;
import java.util.List;
import javax.ejb.Local;
import restJpa.ClientDTO;
import restJpa.CurrenciesDTO;
import restJpa.ManagerDTO;
import restJpa.ResultsDTO;

@Local
public interface IManageQueries {
    
    public String addManager(String name);
    public String addClient(String name, String manager);
    public List<ManagerDTO> listManagers();
    public String addCurrency(String currency, String rate);
    public List<ClientDTO> listClients();
    public List<CurrenciesDTO> listCurrencies();
    public ResultsDTO resultsList();
}
