package si.medius.datascience.transformer;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import si.medius.datascience.entity.Invoice;
import si.medius.datascience.entity.InvoiceStatus;
import si.medius.datascience.entity.Segment;

@ApplicationScoped
public class InvoiceSplitterStream
{
    private static final Logger log = LoggerFactory.getLogger(InvoiceSplitterStream.class);

    @Inject
    StreamConfig streamConfig;

    @Produces
    public Topology buildStream()
    {
        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapperSerde<Invoice> invoiceSerde = new ObjectMapperSerde<>(Invoice.class);
        ObjectMapperSerde<Segment> segmentSerde = new ObjectMapperSerde<>(Segment.class);

        builder.stream(streamConfig.inTopic(), Consumed.with(Serdes.String(), invoiceSerde))
                .filter((k, v) -> v.getStatus() == InvoiceStatus.PENDING && v.getItems() != null)
                .flatMapValues(v -> {
                    List<Segment> segments = new ArrayList<>();
                    for (var i : v.getItems())
                    {
                        Segment segment = new Segment();
                        segment.setInvoiceId(v.getInvoiceNo());
                        segment.setItemId(i.getStockCode());
                        segment.setCustomerId(v.getCustomerID());
                        segments.add(segment);
                    }
                    return segments;
                })
                .to(streamConfig.outTopic(), Produced.with(Serdes.String(), segmentSerde));
        return builder.build();
    }
}
