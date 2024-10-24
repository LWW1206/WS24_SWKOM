<h1>WS24SWKOM Semesterprojekt</h1>

<h2>Application</h2>
The application needs a running Docker Desktop

To start the application
```bash
docker-compose up
```
To start the application after making changes to the code
```bash
docker-compose up --build
```
To stop the application
```bash
docker-compose down
```
This command removes old dangling containers. It is recommended to do this once 
in a while when making changes to the code
 ```bash
docker system prune -f
```
<h2>Once application runs:</h2>

Testing connection \
http://localhost:8080/hello \
http://localhost:8080/hello/urlparam

API Swagger Documentation: \
http://localhost:8080/docs/swagger-ui/index.html