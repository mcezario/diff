# Diff-ed comparator.

It is a project responsible for comparing a left and right (diff-ed) in three different ways:  
1. both sides exactly equals, 
2. length different, 
3. Same length but contents different.

> #### Precondition <br>
> - Mongodb. This project uses Mongodb as database. Follow above the two easiest ways to get the Mongodb installed:
>  1. Docker. Run the command: ```docker-compose -f <path>/docker-compose.yml up --build -d```
>  2. Download and install directly in your compute. Link: https://www.mongodb.com/download-center/community
>
> - Java 8
> - Maven 


## Automated tests

This project had implemented unit and integration tests. 

- ```mvn test``` For running both
- ```mvn -Dtest=*UnitTest test``` Only unit tests
- ```mvn -Dtest=*IntTest test``` Only integration tests  

## Running
1. Start Mongodb. If you are using docker, run the command: ```docker start mongo```
2. Start the application: ```java -jar target/diff-0.0.1-SNAPSHOT.jar```
3. Access swagger endpoint: ```http://localhost:8080/swagger-ui.html```


## Suggestions for improvement
- [ ] HATEOAS
  > Endpoints could be integrated by links. <br>
  For example, the json response of endpoint /v1/diff/<ID>/left could have the link for the endpoint /v1/diff/<ID>/right and vice versa. Also, both could have a link for endpoint <host>/v1/diff/<ID>.
  
- [ ] Cache Strategy -  Netflix Hystrix
  > This solution is saving JSON base64 encoded data in database, Mongodb. <br>
  Let's say that for any reason the database get unavailable, in this case we'd implement a fallback to put the data in cache (In-Memory, [Redis](https://redis.io/), etc...). This could be accomplished using Hystrix to manager exceptions and delegate to a [fallback](https://github.com/Netflix/Hystrix/wiki/How-To-Use#Fallback) method; also, we can use the circuit break benefit from Hystrix.     
- [ ] ELK
  > In terms of dashboard and monitoring, it's possible to implement [ELK Stack](https://www.elastic.co/what-is/elk-stack) to provide rich information about the business, such as quantity of comparisons, quantify of errors, content size, etc. <br>
  It can be solved with [Sleuth](https://spring.io/projects/spring-cloud-sleuth). It is easily integrated with Spring boot.   
- [ ] Purge Data
  > The data are being stored in database. If we consider huge volume of access or a long time without purge the data, it can become a huge collection in terms os data unnecessarily. <br>
  Solving this problem with a schedule solution is a good strategy. 