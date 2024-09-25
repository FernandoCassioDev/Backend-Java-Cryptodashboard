package com.cryptodashboard.Cryptodashboard.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CryptoService {
  private final WebClient webClient;

  public CryptoService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://rest.coinapi.io") // aumenta o limite de buffer para 10mb
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build();
  }

  public Mono<List<String>> getAllCryptos(String apiKey) {
    // Fazendo a requisição para a API
    return this.webClient.get().uri("/v1/assets").header("X-CoinAPI-Key", apiKey)
        // filtra apenas criptomoedas
        .retrieve().bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }).flatMapMany(Flux::fromIterable).filter(asset -> asset.get("type_is_crypto").equals(1))
        .map(asset -> (String) asset.get("asset_id")).collectList();
  }

  //obtem o preço de cada criptomoeda em USD
  public Mono<List<Map<String, Object>>> getCryptoPricesInUSD(String apiKey) {
    return getAllCryptos(apiKey) // obtem todas as criptomoedas
        .flatMapMany(Flux::fromIterable) // converte a lista para fluxo
        .flatMap(crypto -> this.webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/v1/exchangerate/" + crypto + "/USD").build())
            .header("X-coinAPI-Key", apiKey).retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            // Ignora erros de criptomoedas que falharem
            .onErrorResume(e -> Mono.empty()))
            // //filtra apenas criptos com o valor acima de threshold
            // .filter(response -> {
            //   Double rate = (Double) response.get("rate");
            //   return rate != null && rate > threshold;
            // })
        .collectList();
  }

}
