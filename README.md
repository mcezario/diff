#Diff-ed comparator.

It is a project responsible for comparing a left and right (diff-ed) in three different ways:  
1. both sides exactly equals, 
2. length different, 
3. Same length but contents different.

> ####Precondition <br>
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
- [ ] Netflix Hystrix
- [ ] ELK
- [ ] Purge Data