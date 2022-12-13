package si.medius.makeit.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;

import io.smallrye.reactive.messaging.kafka.Record;
import si.medius.makeit.entity.Invoice;

@ApplicationScoped
public class ProduceService
{
    @Inject
    @Channel("invoices")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 1000)
    Emitter<Record<String, Invoice>> invoiceEmitter;

    public void produce(Invoice invoice) {
        Record<String, Invoice> record = Record.of(UUID.randomUUID().toString(), invoice);
        try
        {
            invoiceEmitter.send(record).toCompletableFuture().get(); // Throttle
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }
}
