package com.abc.core.lambda.messmage.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;

import java.util.Map;

@EnableConfigurationProperties(KafkaAppProperties.class)
@Configuration
@EnableKafka
public class KafkaConfig <K,V>{
    @Autowired
    private KafkaAppProperties properties;

    //Kafka配置
    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> producerProperties = properties.buildProducerProperties();
        producerProperties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @ServiceActivator(inputChannel = "toKafka")
    @Bean
    public MessageHandler handler(KafkaTemplate<K, V> kafkaTemplate) {
        KafkaProducerMessageHandler<K, V> handler =
                new KafkaProducerMessageHandler<K, V>(kafkaTemplate);
        handler.setMessageKeyExpression(new LiteralExpression(this.properties.getMessageKey()));
        return handler;
    }

    //消费者配置
    @Bean
    public ConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> consumerProperties = properties
                .buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public KafkaMessageListenerContainer<K, V> container(
            ConsumerFactory<K, V> kafkaConsumerFactory) {
        return new KafkaMessageListenerContainer<>(kafkaConsumerFactory,
                new ContainerProperties(new TopicPartitionInitialOffset(this.properties.getTopic(), 0)));
    }

    @Bean
    public KafkaMessageDrivenChannelAdapter<K, V>
    adapter(KafkaMessageListenerContainer<K, V> container) {
        KafkaMessageDrivenChannelAdapter<K, V> kafkaMessageDrivenChannelAdapter =
                new KafkaMessageDrivenChannelAdapter<>(container);
        kafkaMessageDrivenChannelAdapter.setOutputChannel(fromKafka());
        return kafkaMessageDrivenChannelAdapter;
    }

    @Bean
    public PollableChannel fromKafka() {
        return new QueueChannel();
    }
    ////////////////////////////
}
