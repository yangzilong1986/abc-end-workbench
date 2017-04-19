package com.abc.core.lambda.messmage.kafka;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.Collections;
import java.util.Map;


@SpringBootApplication
public class Application {

	@Autowired
	private KafkaAppProperties properties;

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context
				= new SpringApplicationBuilder(Application.class)
//					.web(WebApplicationType.NONE)
					.run(args);
		context.getBean(Application.class).runDemo(context);
		context.close();
	}

	private void runDemo(ConfigurableApplicationContext context) {
		MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
		System.out.println("Sending 10 messages...");
		Map<String, Object> headers = Collections.singletonMap(KafkaHeaders.TOPIC, this.properties.getTopic());
		for (int i = 0; i < 10; i++) {
			toKafka.send(new GenericMessage<>("foo" + i, headers));
		}
		System.out.println("Sending a null message...");
		toKafka.send(new GenericMessage<>(KafkaNull.INSTANCE, headers));

		PollableChannel fromKafka = context.getBean("fromKafka", PollableChannel.class);
		Message<?> received = fromKafka.receive(10000);
		int count = 0;
		while (received != null) {
			System.out.println(received);
			received = fromKafka.receive(++count < 11 ? 10000 : 1000);
		}

		System.out.println("Adding an adapter for a second topic and sending 10 messages...");
		addAnotherListenerForTopics(this.properties.getNewTopic());
		headers = Collections.singletonMap(KafkaHeaders.TOPIC, this.properties.getNewTopic());
		for (int i = 0; i < 10; i++) {
		    toKafka.send(new GenericMessage<>("bar" + i, headers));
		}
		received = fromKafka.receive(10000);
		count = 0;
		while (received != null) {
		    System.out.println(received);
			received = fromKafka.receive(++count < 10 ? 10000 : 1000);
		}
	}
	////////////////////////////

	@Autowired
	private IntegrationFlowContext flowContext;

	@Autowired
	private KafkaProperties kafkaProperties;

	public void addAnotherListenerForTopics(String... topics) {
		Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
		// change the group id so we don't revoke the other partitions.
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,
				consumerProperties.get(ConsumerConfig.GROUP_ID_CONFIG) + "x");
		IntegrationFlow flow =
			IntegrationFlows
				.from(Kafka.messageDrivenChannelAdapter(
						new DefaultKafkaConsumerFactory<String, String>(consumerProperties), topics))
				.channel("fromKafka")
				.get();
		this.flowContext.registration(flow).register();
	}

}
