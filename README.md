# funCode

docker run -v "$(pwd)":/data --name mongo -p 27017:27017 -d mongo mongod --smallfiles

docker exec -it mongo bash 

mongo

use tweets


sh gradlew gatling-com.simulation.Stuff
