package uk.gov.companieshouse.disqualifiedofficers.search.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.companieshouse.disqualifiedofficers.search.serialization.ResourceChangedDeserializer;
import uk.gov.companieshouse.disqualifiedofficers.search.serialization.ResourceChangedSerializer;
import uk.gov.companieshouse.stream.ResourceChangedData;

@Configuration
@EnableKafka
@Profile("!test")
public class KafkaConfig {


    private final ResourceChangedDeserializer resourceChangedDeserializer;
    private final ResourceChangedSerializer resourceChangedSerializer;

    private final String bootstrapServers;

    /**
     * Constructor.
     */
    public KafkaConfig(ResourceChangedDeserializer resourceChangedDeserializer,
                       ResourceChangedSerializer resourceChangedSerializer,
                       @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.resourceChangedDeserializer = resourceChangedDeserializer;
        this.resourceChangedSerializer = resourceChangedSerializer;
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Kafka Consumer Factory.
     */
    @Bean
    public ConsumerFactory<String, ResourceChangedData> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new ErrorHandlingDeserializer<>(resourceChangedDeserializer));
    }

    /**
     * Kafka Producer Factory.
     */
    @Bean
    public ProducerFactory<String, ResourceChangedData> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ResourceChangedDeserializer.class);
        return new DefaultKafkaProducerFactory<>(
                props, new StringSerializer(), resourceChangedSerializer);
    }

    @Bean
    public KafkaTemplate<String, ResourceChangedData> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Kafka Listener Container Factory.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory
            <String, ResourceChangedData> listenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResourceChangedData> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                ResourceChangedDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return props;
    }
}





