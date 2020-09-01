# StockPriceMicroServiceProject
Micro Service Learning Project, built on Spring cloud using 
 
 1. Service Discovery: Eureka
 2. Circuit Breaker: Hystrix
 3. Client Side Load Balancer: Ribbon
 4. Declarative REST Client: Feign
 5. Router and Filter: Zuul

Took reference from Techprime.

It has 3 individual MS services,
1. DB Service : To fetch and store data in DB.
2. Eureka Service : For Service Registry and Zuul Proxy.
3. Stock Service : Calling DB Service based on the stock input and then calling Yahoo Fincance API to get the price details of stock.
