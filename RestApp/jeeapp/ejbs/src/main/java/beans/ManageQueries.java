package beans;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import restJpa.*;

@Stateless
public class ManageQueries implements IManageQueries {

    @PersistenceContext(unitName = "tables")
    EntityManager em;

    // add new manager to database
    public String addManager(String name){

        Manager newManager = new Manager();
        String result = "Error in ManageQueries while adding new manager";

        // get next unique ID for new manager
        TypedQuery<Manager> tq = em.createQuery("select m from Manager m", Manager.class);
        List<Manager> mList = tq.getResultList();

        // if it's the first entry in the table
        if (mList.size() == 0) {
           
            newManager = new Manager(1, name, 0.0);
            em.persist(newManager);
            result = "New manager created!";
        }
        else {

            Query q = em.createQuery("select max(id) from Manager");
            int newID = (int) q.getResultList().get(0) + 1;
            newManager = new Manager(newID, name, 0.0);
            em.persist(newManager);
            result = "New manager created!";
        }

        return result;
    }

    // add new client to database
    public String addClient(String name, String manager){

        Client newClient = new Client();
        String result = "Error while adding new client:";

        // get menager entity
        List<Manager> clientManager = null;
        
        TypedQuery<Manager> tqm = em.createQuery("select m from Manager m where m.id = ?1", Manager.class).setParameter(1, Integer.parseInt(manager));
        clientManager = tqm.getResultList();
    
        if(Integer.toString(clientManager.size()).equals("1")){

            // get manager entity
            Manager myManager = em.find(Manager.class, clientManager.get(0).getId());

            // get next unique ID for new client
            TypedQuery<Client> tqc = em.createQuery("select c from Client c", Client.class);
            List<Client> mList = tqc.getResultList();

            // if it's the first entry in the table
            if (mList.size() == 0) {
                
                newClient = new Client(1, name, myManager, 0.0, 0.0, 0.0, 0.0, 0);
                em.persist(newClient);
                result = "New client created!";
            }
            else {

                Query q = em.createQuery("select max(id) from Client");
                int newID = (int) q.getResultList().get(0) + 1;
                newClient = new Client(newID, name, myManager, 0.0, 0.0, 0.0, 0.0, 0);
                em.persist(newClient);
                result = "New client created!";
            }

            // update manager client list
            List<Client> mangerClients = myManager.getMyClients();
            mangerClients.add(newClient);
            myManager.setMyClients(mangerClients);

        } 
        else{
            result += "\nInvalid manager id!";
        }

        return result;
    }

    // add currency
    public String addCurrency(String currency, String rate){

        String result = "Error in ManageQueries while adding new currency";

        // check if cuerrency already exists
        TypedQuery<Currencies> tq = em.createQuery("select c from Currencies c where c.currency = ?1", Currencies.class).setParameter(1, currency);
        List<Currencies> cList = tq.getResultList();

        if(cList.isEmpty()){
            Currencies newCurrency = new Currencies(currency, Double.parseDouble(rate));
            em.persist(newCurrency);
            result = "New currency created!";
        }
        else{
            result += "\nThis currency already exists!";
        }

        return result;
    }

    // list managers
    public List<ManagerDTO> listManagers(){

        List<ManagerDTO> managerList = new ArrayList<>();
        
        // get entity from data base 
        TypedQuery<Manager> tq = em.createQuery("select m from Manager m", Manager.class);
        List<Manager> mList = tq.getResultList();

        // pass data do DTO objects
        for(Manager m: mList){
            ManagerDTO mDto = new ManagerDTO(m.getId(), m.getName(), m.getRevenue());
        
            // get managers client list
            List<ClientDTO> cListDto = new ArrayList<>();
            for(Client c: m.getMyClients()){
                ClientDTO cDto = new ClientDTO(c.getId(), c.getName(), null, c.getCredits(), c.getPayments(), c.getBalance(), c.getLast_month_bill(), c.getPayments_made_in_past_hour());
                cListDto.add(cDto);
            }
            mDto.setMyClients(cListDto);

            // add manager DTO do manager DTO list
            managerList.add(mDto);
        }

        return managerList;
    }

    // list clients
    public List<ClientDTO> listClients(){

        List<ClientDTO> clientList = new ArrayList<>();
        
        TypedQuery<Client> tq = em.createQuery("select c from Client c", Client.class);
        List<Client> cList = tq.getResultList();

        for(Client c: cList){

            // get manager
            ManagerDTO mDto = new ManagerDTO(c.getMyManager().getId(), c.getMyManager().getName(), c.getMyManager().getRevenue());
            ClientDTO cDto = new ClientDTO(c.getId(), c.getName(), mDto, c.getCredits(), c.getPayments(), c.getBalance(), c.getLast_month_bill(), c.getPayments_made_in_past_hour());
            
            clientList.add(cDto);
        }

        return clientList;
    }

    // list currencies
    public List<CurrenciesDTO> listCurrencies(){

        List<CurrenciesDTO> currencyList = new ArrayList<>();
        
        TypedQuery<Currencies> tq = em.createQuery("select c from Currencies c", Currencies.class);
        List<Currencies> cList = tq.getResultList();

        for(Currencies c: cList){

            CurrenciesDTO cDto = new CurrenciesDTO(c.getCurrency(), c.getRate());
            currencyList.add(cDto);
        }

        return currencyList;
    }

     // list results
     public ResultsDTO resultsList(){

        TypedQuery<Results> tq = em.createQuery("select r from Results r where r.id = '-1'", Results.class);
        Results r = tq.getResultList().get(0);
        if(r != null){
            ResultsDTO rDto = new ResultsDTO(r.getTotal_credits(), r.getTotal_payments(), r.getTotal_balance(), r.getClient_w_biggest_debt(), r.getManager_w_biggest_revenue());
            return rDto;
        }
        else{
            return null;
        }
    }
}