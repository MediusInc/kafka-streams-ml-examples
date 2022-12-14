package si.medius.datascience.kafka;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import si.medius.datascience.entity.Invoice;
import si.medius.datascience.entity.InvoiceStatus;
import si.medius.datascience.entity.Item;

@Path("/produce")
public class ProduceResource
{
    @Inject
    ProduceService produceService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{invoiceNo}")
    public Response createNewInvoice(@PathParam("invoiceNo")String invoiceNo, Invoice invoice) {
        invoice.setInvoiceNo(invoiceNo);
        produceService.produce(invoice);
        return Response.ok().entity(invoice).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDateTime.of(2022, 1, 1, 13, 0));
        invoice.setInvoiceNo("601588");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCountry("FR");
        invoice.setCustomerID("12680");

        Item item = new Item();
        item.setUnitPrice(1.65);
        item.setQuantity(12);
        item.setStockCode("22556");
        item.setDescription("PLASTERS IN TIN CIRCUS PARADE");

        invoice.addItem(item);

        produceService.produce(invoice);

        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/recommended")
    public Response createNewInvoiceRecommended() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(LocalDateTime.of(2022, 1, 1, 12, 0));
        invoice.setInvoiceNo("601587");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setCountry("FR");
        invoice.setCustomerID("12680");

        Item item = new Item();
        item.setUnitPrice(1.65);
        item.setQuantity(8);
        item.setStockCode("22551");
        item.setDescription("PLASTERS IN TIN SPACEBOY");

        invoice.addItem(item);

        produceService.produce(invoice);

        return Response.ok().build();
    }
}
