package ru.vichukano.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.*;

import java.util.function.Consumer;

@SpringBootApplication
public class DemoApp {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(DemoApp.class, args);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(KafkaProperties kafkaProperties) {
        kafkaProperties.setClientId("demo-app");
        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
        return new KafkaTemplate<>(pf, kafkaProperties.buildProducerProperties());
    }

    @Bean
    public Consumer<Flux<String>> process(KafkaTemplate<String, String> kafkaTemplate) {
        return flux -> flux
                .flatMap(f -> Mono.just("FOOBAR"))
                .map(String::toUpperCase)
                .map(mes -> kafkaTemplate.send("output", mes))
                .subscribe();
    }
}
