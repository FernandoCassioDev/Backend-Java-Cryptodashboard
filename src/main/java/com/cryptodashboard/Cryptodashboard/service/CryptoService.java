package com.cryptodashboard.Cryptodashboard.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class CryptoService {
  private final WebClient webClient;

  public CryptoService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://rest.coinapi.io") // aumenta o limite de buffer para 10mb
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build();
  }

  public Mono<List<Map<String, Object>>> getAllCryptos(String apiKey) {
    // Fazendo a requisição para a API
    return this.webClient.get().uri("/v1/assets").header("X-CoinAPI-Key", apiKey)
        // filtra apenas criptomoedas
        .retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).map(assets ->  assets.stream().filter(asset -> asset.get("price_usd") != null).collect(Collectors.toList()));
  }

   // obtem as criptos acima do preço min em USD
  public Mono<List<Map<String, Object>>> getCryptoPricesInUSD(String apiKey, double minPrice) {
    return getAllCryptos(apiKey) // obtem todas as criptomoedas
        .map(assets -> assets.stream().filter(asset -> {
          // garante que o preço realmente está em USD
          Double price = (Double) asset.get("price_usd");
          return price != null && price > minPrice;
        })
            .collect(Collectors.toList()));
  }

}
