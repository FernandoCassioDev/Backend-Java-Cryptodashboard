package com.cryptodashboard.Cryptodashboard.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptodashboard.Cryptodashboard.service.CryptoService;

import reactor.core.publisher.Mono;

//Define a classe como um controlador REST
@RestController
public class CryptoController {

  private final CryptoService cryptoService;

  //Construtor que injeta o serviço de criptomoedas
  public CryptoController(CryptoService cryptoService) {
    this.cryptoService = cryptoService;
  }

  //Define o endpoint GET
  @GetMapping("/cryptos")
  public Mono<List<Map<String, Object>>> getCryptos(
    //Recebe a chave da API no cabeçalho da requisição
    @RequestHeader("X-coinAPI-Key") String apiKey,
    //recebe o valor mínimo da criptomoeda via query param,
    //com o valor padrão 10
    @RequestParam(defaultValue = "10") double minPrice) {

    //chama o serviço para obter as criptomoedas filtradas
    return cryptoService.getCryptoPricesInUSD(apiKey, minPrice);
  }

}
