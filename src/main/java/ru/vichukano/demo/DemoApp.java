package ru.vichukano.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Function;

@RestController
@SpringBootApplication
public class DemoApp {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(DemoApp.class, args);
    }

    @Bean
    public Function<Flux<String>, Flux<String>> process(WebClient webClient) {
        return flux -> flux
                .flatMap(f -> getBar(webClient, f))
                .map(String::toUpperCase);
    }

    @PostMapping(path = "/bar")
    public String bar(@RequestBody String body) {
        return body + "bar";
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(1));
        return WebClient.builder()
                .baseUrl("http://localhost:8080/")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private Mono<String> getBar(WebClient client, String body) {
        return client.post()
                .uri("/bar")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

}
