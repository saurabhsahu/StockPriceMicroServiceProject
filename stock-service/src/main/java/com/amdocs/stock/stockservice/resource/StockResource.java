package com.amdocs.stock.stockservice.resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amdocs.stock.stockservice.config.StockPriceExchangeFeignProxy;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.stock.StockQuote;

//@Service
@RestController
@RequestMapping("/rest/stock")
public class StockResource {

    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    StockPriceExchangeFeignProxy stockPriceExchangeFeignProxy;
    
    private static String DUMMY="Dummy";

    @HystrixCommand(fallbackMethod = "getStock_Fallback",
    commandProperties = {
    	       @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
    	       @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value="60")
    	    })
    @GetMapping("/{username}")
    public @ResponseBody List<Quote> getStock(@PathVariable("username") final String userName) {

        ResponseEntity<List<String>> quoteResponse = restTemplate.exchange("http://db-service/rest/db/" + userName, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<String>>() {
                });


        List<String> quotes = quoteResponse.getBody();
        return quotes
                .stream()
                .map(quote -> {
                    Stock stock = getStockPrice(quote);
                    return new Quote(quote, stock.getQuote().getPrice());
                })
                .collect(Collectors.toList());
    }
    
    @GetMapping("/feign/{username}")
    public @ResponseBody List<Quote> getStockViaFeign(@PathVariable("username") final String userName) {

        List<String> quotes = stockPriceExchangeFeignProxy.retrieveQuotes(userName);
        return quotes
                .stream()
                .map(quote -> {
                    Stock stock = getStockPrice(quote);
                    return new Quote(quote, stock.getQuote().getPrice());
                })
                .collect(Collectors.toList());
    }

    private Stock getStockPrice(String quote) {
        try {
        	System.setProperty("https.proxyHost", "genproxy.amdocs.com");
			System.setProperty("https.proxyPort", "8080");
            return YahooFinance.get(quote);
        } catch (IOException e) {
            e.printStackTrace();
            Stock stock = new Stock(quote);
            stock.setQuote(new StockQuote(quote));
            stock.getQuote().setPrice(new BigDecimal(55.11));
            return stock;
        }
    }
    
    public List<Quote> getStock_Fallback(String message) {
    	 Stock stock = new Stock(DUMMY);
         stock.setQuote(new StockQuote(DUMMY));
         BigDecimal bd = new BigDecimal("00.00");
         bd.setScale(2, BigDecimal.ROUND_UP);
         stock.getQuote().setPrice(bd);
         List<Quote> quoteList = new ArrayList<>();
         quoteList.add( new Quote(DUMMY, stock.getQuote().getPrice()));
         return quoteList;
      }


    private class Quote {
        private String quote;
        private BigDecimal price;

        public Quote(String quote, BigDecimal price) {
            this.quote = quote;
            this.price = price;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
