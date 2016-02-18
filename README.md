# Spring_Neo4j_health_monitor

A big part of the DevOps responsibilities is to monitor and maintain the health of running servers. 
If a production server goes down, appropriate actions must be undertaken to bring the service back to life. 
However, before any resurrection, one must know that the server is malfunctioning in the first place. 
In an automated cloud environment, this can be handled by a load balancer calling a known endpoint. 
For this reason, Spring Boot has a /health endpoint that is part of its Actuator features. 

Spring Boot brings along some cool production ready features. Among them is the so called health checks. 
They are a great way to give you a quick overview about the system state. 
Is the database up and running, is my disk running out of space? 

Any HealthIndicator Spring bean will contribute to the overall health status presented at the /health endpoint. 
By default spring boot configure application to be accessible on the 8080 port. If you didn't specify the port 
then your health service URL is http://your.domain.com:8080/health.
 
Many NoSql HealthIndicators are auto-configured by Spring Boot:
CassandraHealthIndicator, MongoHealthIndicator, RabbitHealthIndicator and RedisHealthIndicator.

To provide custom health information you can register Spring beans that implement the HealthIndicator interface. 
You need to provide an implementation of the health() method and return a Health response. The Health response 
should include a status and can optionally include additional details to be displayed.

Neo4jServerHealthIndicator is a custom health indicator to check Neo4j cluster servers are up and running.

Neo4jDatabaseHealthIndicator is a custom health indicator to check Neo4j database is up and running.
