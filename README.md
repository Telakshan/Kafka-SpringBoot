## Step 1:

docker-compose up -d

After starting the spring boot producer

### Run this command to see the topics created.

 - docker exec zookeeper kafka-topics --bootstrap-server kafka2:9092 --list
 
 ## Step 2:
 
 ### If you want to see the all the topics that you've published since the beginning.

 docker exec --interactive --tty kafka1 kafka-console-consumer --bootstrap-server kafka1:9092 --topic library-events --from-beginning

 ### Three seperate brokers
 
 docker exec --interactive --tty kafka2 kafka-console-consumer --bootstrap-server kafka2:9092 --topic library-events

 docker exec --interactive --tty kafka3 kafka-console-consumer --bootstrap-server kafka3:9092 --topic library-events

 docker exec --interactive --tty kafka1 kafka-console-consumer --bootstrap-server kafka1:9092 --topic library-events
 
 ### Step 3:
 
 Start Spring Boot Application

