package com.cryptodashboard.Cryptodashboard.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

// Define esta classe como um serviço no contexto do spring
@Service
public class CryptoService {

  private final WebClient webClient;

  // Construtor que inicializa o WebClient com a URL base da CoinAPI
  public CryptoService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://rest.coinapi.io") // aumenta o limite de buffer para 20mb
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)).build();
  }

  // método que faz a requisição para API e retorna todas as criptomoedas
  public Mono<List<Map<String, Object>>> getAllCryptos(String apiKey) {
    // Fazendo a requisição para a API
    return this.webClient.get()
        // EndPoint que retorna todos os ativos
        .uri("/v1/assets")
        // Autenticação via chave da API
        .header("X-CoinAPI-Key", apiKey)
        // Recupera a resposta
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
        })
        // Filtra apenas ativos que possuem preço em USD
        .map(assets -> assets.stream().filter(asset -> asset.get("price_usd") != null).collect(Collectors.toList()));
  }

  // Método que retorna as criptomoedas com preços acima de um valor específico
  public Mono<List<Map<String, Object>>> getCryptoPricesInUSD(String apiKey, double minPrice) {
    return getAllCryptos(apiKey) // obtem todas as criptomoedas
        .map(assets -> assets.stream().filter(asset -> {

          // Verifica se o preço em USD existe e é maior que o valor minimo

          Double price = (Double) asset.get("price_usd");
          return price != null && price > minPrice;
        })
            .collect(Collectors.toList()));
  }

}
