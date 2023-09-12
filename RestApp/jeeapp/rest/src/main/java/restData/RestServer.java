package restData;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import beans.IManageQueries;

@Transactional
@RequestScoped
@Path("/myservice")
@Produces(MediaType.APPLICATION_JSON)
public class RestServer {

    @EJB
    private IManageQueries mq;

    @GET
    @Path("/addManager")
    public String addManager(@QueryParam("input") String name) {
        return mq.addManager(name);
    }

    @GET
    @Path("/addClient")
    public String addClient(@QueryParam("input") String name, @QueryParam("manager") String manager) {
        return mq.addClient(name, manager);
    }

    @GET
    @Path("/addCurrency")
    public String addCurrency(@QueryParam("input") String currency, @QueryParam("rate") String rate) {
        return mq.addCurrency(currency, rate);
    }

    @GET
    @Path("/listManagers")
    public Response listManagers() {
        return Response.ok().entity(mq.listManagers()).build();
    }

    @GET
    @Path("/listClients")
    public Response listClients() {
        return Response.ok().entity(mq.listClients()).build();
    }

    @GET
    @Path("/listCurrencies")
    public Response listCurrencies() {
        return Response.ok().entity(mq.listCurrencies()).build();
    }

    @GET
    @Path("/resultsList")
    public Response resultsList() {
        return Response.ok().entity(mq.resultsList()).build();
    }
}