# StockPriceMicroServiceProject
Micro Service Learning Project, built on Spring cloud using 
  Enable common patterns with just annotations:
  Service Discovery: Eureka
  Circuit Breaker: Hystrix
  Client Side Load Balancer: Ribbon
  Declarative REST Client: Feign
  Router and Filter: Zuul

Took reference from Techprime.

It has 3 individual MS services,
1. DB Service : To fetch and store data in DB.
2. Eureka Service : For Service Registry and Zuul Proxy.
3. Stock Service : Calling DB Service based on the stock input and then calling Yahoo Fincance API to get the price details of stock.
