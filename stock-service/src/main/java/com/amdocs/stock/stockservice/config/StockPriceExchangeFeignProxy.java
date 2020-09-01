package com.amdocs.stock.stockservice.config;


import java.util.List;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name="db-service", url="localhost:8300")
@FeignClient(name="db-service")
@RibbonClient(name="db-service")
public interface StockPriceExchangeFeignProxy {
  @GetMapping("/rest/db/{username}")
  public List<String> retrieveQuotes(@PathVariable("username") final String username);
}

