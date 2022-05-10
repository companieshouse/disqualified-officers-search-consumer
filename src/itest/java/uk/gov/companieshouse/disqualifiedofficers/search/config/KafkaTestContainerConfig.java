package uk.gov.companieshouse.disqualifiedofficers.search.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.disqualifiedofficers.search.serialization.ResourceChangedDeserializer;
import uk.gov.companieshouse.disqualifiedofficers.search.serialization.ResourceChangedSerializer;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.stream.ResourceChangedData;

@TestConfiguration
public class KafkaTestContainerConfig {

    @MockBean
    private CHKafkaProducer chKafkaProducer;

    @Value("${disqualified-officers.search.group-id}")
    private String groupId;

    private final ResourceChangedDeserializer resourceChangedDeserializer;
    private final ResourceChangedSerializer resourceChangedSerializer;

    @Autowired
    public KafkaTestContainerConfig(ResourceChangedDeserializer resourceChangedDeserializer,
                                    ResourceChangedSerializer resourceChangedSerializer) {
        this.resourceChangedDeserializer = resourceChangedDeserializer;
        this.resourceChangedSerializer = resourceChangedSerializer;
    }

    @Bean
    public KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        kafkaContainer.start();
        return kafkaContainer;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ResourceChangedData> listenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ResourceChangedData> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ResourceChangedData> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(kafkaContainer()),
                new StringDeserializer(),
                resourceChangedDeserializer);
    }

    @Bean
    public Map<String, Object> consumerConfigs(KafkaContainer kafkaContainer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ResourceChangedDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return props;
    }

    @Bean
    public ProducerFactory<String, ResourceChangedData> producerFactory(KafkaContainer kafkaContainer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ResourceChangedSerializer.class);
        DefaultKafkaProducerFactory<String, ResourceChangedData> factory = new DefaultKafkaProducerFactory<>(
                configProps, new StringSerializer(), resourceChangedSerializer);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, ResourceChangedData> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(kafkaContainer()));
    }

}