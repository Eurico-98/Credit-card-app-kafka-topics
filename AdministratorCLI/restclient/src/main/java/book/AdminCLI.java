package book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class AdminCLI {
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        Client client = ClientBuilder.newClient();
        WebTarget target = null;
        Response response = null;

        System.out.println("Select an option:"+
                               "\n1 - Add managers to data base;"+
                               "\n2 - Add clients to data base;"+
                               "\n3 - Add currency to data base;"+
                               "\n4 - List managers;"+
                               "\n5 - List clients;"+
                               "\n6 - List currencies;"+
                               "\n7 - List credits per client;"+
                               "\n8 - List payments per client;"+
                               "\n9 - List balance per client;"+
                               "\n10 - Print total credits;"+
                               "\n11 - Print total payments;"+
                               "\n12 - Print total balance;"+
                               "\n13 - Last month bill per client;"+
                               "\n14 - List of clients without payment in the last 2 months;"+
                               "\n15 - Client with biggest debt;"+
                               "\n16 - Manager with biggest revenue;"+
                               "\n17 - Exit;\n");
        try {
            input = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        while(true){

            if(input.equals("1")){

                try {
                    System.out.print("Input manager name: ");
                    input = reader.readLine();
                    target = client.target("http://wildfly:8080/rest/services/myservice/addManager");
                    response = target.queryParam("input", input).request().get();
                    System.out.println(response.readEntity(String.class));
                    response.close();
                    System.out.println("------------------------------------\n");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(input.equals("2")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listManagers");
                response = target.request().get();
                List<ManagerDTO> managersList = response.readEntity(new GenericType<List<ManagerDTO>>(){});

                if(!managersList.isEmpty()){

                    try {
                        System.out.print("Input client name: ");
                        input = reader.readLine();
                        
                        // print only ID and name for this listing
                        System.out.println("------- Managers List -------");
                        for(ManagerDTO mdto: managersList){
                            System.out.println("ID: "+mdto.getId());
                            System.out.println("Name: "+mdto.getName()+"\n#");
                        }
                        System.out.println("-----------------------------");

                        response.close();
                        System.out.print("Select a manager for this client: ");
                        String manager = reader.readLine();
                        Integer.parseInt(manager);

                        target = client.target("http://wildfly:8080/rest/services/myservice/addClient");
                        response = target.queryParam("input", input).queryParam("manager", manager).request().get();
                        System.out.println(response.readEntity(String.class));
                        response.close();
                        System.out.println("------------------------------------\n");

                    } catch (NumberFormatException | IOException e) {
                        System.out.println("Invalid manager id!");
                    }
                }
                else {
                    System.out.println("There are no managers registered, regiter a manager first!");
                }
            }
            
            if(input.equals("3")){

                try {
                    System.out.print("Input currency name: ");
                    input = reader.readLine();
                    System.out.print("Input rate: ");
                    String rate = reader.readLine();
                    Double.parseDouble(rate);
                    target = client.target("http://wildfly:8080/rest/services/myservice/addCurrency");
                    response = target.queryParam("input", input).queryParam("rate", rate).request().get();
                    System.out.println(response.readEntity(String.class));
                    response.close();
                    System.out.println("------------------------------------\n");

                } catch (NumberFormatException | IOException e) {
                    System.out.println("Invale rate, must be double, example: 1.0!");
                }
            }

            if(input.equals("4")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listManagers");
                response = target.request().get();
                List<ManagerDTO> managersList = response.readEntity(new GenericType<List<ManagerDTO>>(){});

                System.out.println("----------- Managers List ----------");
                if(managersList.size() > 0){
                    for(ManagerDTO mdto: managersList){
                        System.out.println("ID: " + mdto.getId());
                        System.out.println("Name: "+mdto.getName());
                        System.out.println("Client list:");
                        for(ClientDTO cdto: mdto.getMyClients()){
                            System.out.println("  -"+cdto.getName());
                        }
                        
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No managers registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("5")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                System.out.println("----------- Clients List -----------");
                if(clientList.size() > 0){
                    for(ClientDTO cdto: clientList){
                        System.out.println("ID: " + cdto.getId());
                        System.out.println("Name: "+cdto.getName());
                        System.out.println("Manager: "+cdto.getMyManager().getName());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("6")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listCurrencies");
                response = target.request().get();
                List<CurrenciesDTO> currencyList = response.readEntity(new GenericType<List<CurrenciesDTO>>(){});

                System.out.println("----------- Currency List ----------");
                if(currencyList.size() > 0){
                    for(CurrenciesDTO cdto: currencyList){
                        System.out.println(cdto.getCurrency());
                        System.out.println("Rate: "+cdto.getRate());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No currencies available!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("7")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientCreditList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                System.out.println("-------- Credits per client --------");
                if(clientCreditList.size() > 0){
                    for(ClientDTO cdto: clientCreditList){
                        System.out.println(cdto.getName());
                        System.out.println("Credit: "+cdto.getCredits());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("8")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientCreditList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                System.out.println("------- Payments per client --------");
                if(clientCreditList.size() > 0){
                    for(ClientDTO cdto: clientCreditList){
                        System.out.println(cdto.getName());
                        System.out.println("Payments: "+cdto.getPayments());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("9")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientCreditList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                System.out.println("-------- Balance per client --------");
                if(clientCreditList.size() > 0){
                    for(ClientDTO cdto: clientCreditList){
                        System.out.println(cdto.getName());
                        System.out.println("Balance: "+cdto.getBalance());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("10")){

                target = client.target("http://wildfly:8080/rest/services/myservice/resultsList");
                response = target.request().get();
                try{
                    ResultsDTO rDto = response.readEntity(ResultsDTO.class);
                    System.out.println("----------- Total credits ----------\n"+
                                    rDto.getTotal_credits()+
                                    "\n------------------------------------\n");
                } catch (ProcessingException e){
                    System.out.println("No results available!\n");
                }
            }

            if(input.equals("11")){

                target = client.target("http://wildfly:8080/rest/services/myservice/resultsList");
                response = target.request().get();
                try{
                    ResultsDTO rDto = response.readEntity(ResultsDTO.class);
                    System.out.println("---------- Total payments ----------\n"+
                                    rDto.getTotal_payments()+
                                    "\n------------------------------------\n");
                } catch (ProcessingException e){
                    System.out.println("No results available!\n");
                }
            }

            if(input.equals("12")){

                target = client.target("http://wildfly:8080/rest/services/myservice/resultsList");
                response = target.request().get();
                try{
                    ResultsDTO rDto = response.readEntity(ResultsDTO.class);
                    System.out.println("----------- Total balance ----------\n"+
                                    rDto.getTotal_balance()+
                                    "\n------------------------------------\n");
                } catch (ProcessingException e){
                    System.out.println("No results available!\n");
                }                   
            }

            if(input.equals("13")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientCreditList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                System.out.println("---- Last month bill per client ----");
                if(clientCreditList.size() > 0){
                    for(ClientDTO cdto: clientCreditList){
                        System.out.println(cdto.getName());
                        System.out.println("Bill: "+cdto.getLast_month_bill());
                        System.out.println("#");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("------------------------------------\n");
            }

            if(input.equals("14")){

                target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                response = target.request().get();
                List<ClientDTO> clientList = response.readEntity(new GenericType<List<ClientDTO>>(){});
                int count = 0;
                System.out.println("- Clients without payment in the last 2 months -");
                if(clientList.size() > 0){
                    for(ClientDTO cdto: clientList){

                        if(cdto.getPayments_made_in_past_hour() == 0){
                            System.out.println("ID: " + cdto.getId());
                            System.out.println("Name: "+cdto.getName());
                            System.out.println("#");
                            count++;
                        }
                    }

                    if(count == 0){
                        System.out.println("No clients with 0 payments in the last 2 months!");
                    }
                }
                else{
                    System.out.println("No clients registered!");
                }
                System.out.println("-----------------------------------------\n");
            }

            if(input.equals("15")){

                target = client.target("http://wildfly:8080/rest/services/myservice/resultsList");
                response = target.request().get();
                try{
                    ResultsDTO rDto = response.readEntity(ResultsDTO.class);

                    target = client.target("http://wildfly:8080/rest/services/myservice/listClients");
                    response = target.request().get();
                
                    List<ClientDTO> clientList = response.readEntity(new GenericType<List<ClientDTO>>(){});

                    String result = "No results available!";

                    System.out.println("----- Client with biggest debt -----");
                    if(clientList.size() > 0){
                        for(ClientDTO cdto: clientList){
                            if(rDto.getClient_w_biggest_debt() == cdto.getId()){
                                System.out.println("ID: " + cdto.getId());
                                System.out.println("Name: "+cdto.getName());
                                System.out.print("Debt: "+cdto.getBalance());
                                result = "";
                                break;
                            }
                        }
                    }
                    else{
                        System.out.println("No clients registered!\n");
                    }
                    System.out.print(result);
                    System.out.println("\n------------------------------------\n");
                } catch (ProcessingException e){
                    System.out.println("No results available!");
                }
            }

            if(input.equals("16")){

                target = client.target("http://wildfly:8080/rest/services/myservice/resultsList");
                response = target.request().get();
                try{
                    ResultsDTO rDto = response.readEntity(ResultsDTO.class);

                    target = client.target("http://wildfly:8080/rest/services/myservice/listManagers");
                    response = target.request().get();
                    List<ManagerDTO> managersList = response.readEntity(new GenericType<List<ManagerDTO>>(){});

                    String result = "No results available!";

                    System.out.println("--- Manager with biggest revenue ---");
                    if(managersList.size() > 0){
                        for(ManagerDTO mdto: managersList){
                            if(rDto.getManager_w_biggest_revenue() == mdto.getId()){
                                System.out.println("ID: " + mdto.getId());
                                System.out.println("Name: "+mdto.getName());
                                System.out.print("Revenue: "+mdto.getRevenue());
                                result = "";
                                break;
                            }
                        }
                    }
                    else{
                        System.out.println("No managers registered!");
                    }
                    System.out.print(result);
                    System.out.println("\n------------------------------------\n");
                } catch (ProcessingException e){
                    System.out.println("No results available!\n");
                }
            }

            if(input.equals("17")) break;

            try {
                System.out.print("Select an option: ");
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
