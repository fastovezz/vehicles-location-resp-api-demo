# Getting Started
### Prerequisetis
* Java 8 
* MySQL 8

### Guides
#### DB setup
Pull MySQL docker image (latest, 8 by default)
```
$ docker pull mysql  
```
Temporary fix to `Error response from daemon: cgroups: cgroup mountpoint does not exist: unknown` on linux systems:  
```
$ sudo mkdir /sys/fs/cgroup/systemd  
$ sudo mount -t cgroup -o none,name=systemd cgroup /sys/fs/cgroup/systemd
```
Create MySQL container and create `vehicles` database
```
$ docker run --detach --name demo-mysql -p 33060:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:latest  
$ docker exec -u root -it <container id> /bin/bash  
# mysql -u root -p  
> create database vehicles;
Query OK, 1 row affected (0.02 sec)
```
#### Running app
To run app locally on you computer 
```
./gradlew bootRun
```
* Swagger UI is accessible via [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)
* Swagger API documentation is accessible via [http://localhost:8080/v2/api-docs](http://localhost:8080/v2/api-docs)

### Additional Links
These additional references should also help you:

* [MySQL 8 Spatial Data Types](https://dev.mysql.com/doc/refman/8.0/en/spatial-types.html)
* [MySQL 8 Spatial Function Reference](https://dev.mysql.com/doc/refman/8.0/en/spatial-function-reference.html)
* [Mapping Spatial DB Data Types to JPA Entities. MySQL 8](https://stackoverflow.com/questions/31440496/hibernate-spatial-5-geometrytype)
* [JPA Spatial Functions](https://www.baeldung.com/hibernate-spatial)
