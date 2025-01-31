<h1>WS24SWKOM Semesterprojekt</h1>

<h2>Start Application</h2>

The application needs a running Docker Desktop. Do a mvn clean and mvn package first in DMS and paperless-services. 
```bash
docker-compose up
```

<h2>Stop Application</h2>

```bash
docker-compose down
```
This also removes all volumes

```bash
docker-compose down -v
```

<h2>Quality of life</h2>
This command removes old dangling containers. Can also fix issues with starting the application.

 ```bash
docker system prune -f
```
This command removes unused volumes

 ```bash
docker volume prune
```
<h2>Once application runs:</h2>

Paperless Web UI: \
http://localhost/

API Swagger Documentation: \
http://localhost:8081/docs/swagger-ui/index.html

RabbitMQ: \
http://localhost:15672/

MinIo: \
http://localhost:9090/login
