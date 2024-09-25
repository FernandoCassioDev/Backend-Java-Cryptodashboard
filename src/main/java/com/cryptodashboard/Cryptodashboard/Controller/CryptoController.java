package com.cryptodashboard.Cryptodashboard.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cryptodashboard.Cryptodashboard.service.CryptoService;

import reactor.core.publisher.Mono;

@RestController
public class CryptoController {

  private final CryptoService cryptoService;

  public CryptoController(CryptoService cryptoService) {
    this.cryptoService = cryptoService;
  }

  @GetMapping("/cryptos")
  public Mono<List<Map<String, Object>>> getCryptos(@RequestHeader("X-coinAPI-Key") String apiKey) {

    return cryptoService.getCryptoPricesInUSD(apiKey);
  }

}
