package si.medius.datascience.kafka;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import io.quarkus.runtime.StartupEvent;
import si.medius.datascience.entity.Invoice;
import si.medius.datascience.entity.InvoiceStatus;
import si.medius.datascience.entity.Item;

@ApplicationScoped
public class StoreEventProducer
{
    @Inject
    ProduceService produceService;

    @ConfigProperty(name = "data.file", defaultValue = "data/data.csv")
    String file;

    void onStart(@Observes StartupEvent event)
    {
        Random r = new Random(42);
        List<Invoice> invoices = csvToInvoices(file);
        EnumMap<InvoiceStatus, Integer> statusCount = new EnumMap(InvoiceStatus.class);
        for(var invoice : invoices) {
            int n = r.nextInt(0, 50);
            statusCount.put(InvoiceStatus.PENDING, statusCount.getOrDefault(InvoiceStatus.PENDING, 0) + 1);
            produceService.produce(invoice);
            if(n < 49) {
                invoice.setStatus(InvoiceStatus.READY);
                invoice.setInvoiceDate(invoice.getInvoiceDate().plusDays(1));
                produceService.produce(invoice);
                statusCount.put(InvoiceStatus.READY, statusCount.getOrDefault(InvoiceStatus.READY, 0) + 1);

            }
            if (n < 47) {
                invoice.setStatus(InvoiceStatus.DELIVERED);
                invoice.setInvoiceDate(invoice.getInvoiceDate().plusDays(1));
                produceService.produce(invoice);
                statusCount.put(InvoiceStatus.DELIVERED, statusCount.getOrDefault(InvoiceStatus.DELIVERED, 0) + 1);
            }
        }
    }
    private List<Invoice> csvToInvoices(String file)
    {
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
        Map<String, Invoice> invoices = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(file)))
        {
            csvReader.readNext();
            String[] data = null;
            while ((data = csvReader.readNext()) != null)
            {
                data[7] = countries.get(data[7]);
                Invoice invoice = invoices.getOrDefault(data[0], Invoice.fromCSVLine(data));
                Item item = Item.fromCSVLine(data);
                if(item.getQuantity() < 0 || invoice.getCustomerID() == null) {
                    continue;
                }
                invoice.addItem(item);
                invoices.put(data[0], invoice);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (CsvValidationException e)
        {
            throw new RuntimeException(e);
        }
        return invoices.values().stream().toList();
    }
}
