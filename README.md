<h1>WS24SWKOM Semesterprojekt</h1>

<h2>Application</h2>
The application needs a running Docker Desktop

To start the application
```bash
docker-compose up
```
Run this command to setup a user for the Paperless Server. \
The user is saved in the public db in the auth_user table
```bash
docker compose run --rm webserver createsuperuser
```
To start the application after making changes to the code of the Rest Server
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
This command removes unused volumes
 ```bash
docker volume prune
```
<h2>Once application runs:</h2>

<h3>Paperless Web UI</h3>
http://localhost/

<h3>Rest server</h3>
Testing connection to Rest Server \
http://localhost:8081/hello \
http://localhost:8081/hello/urlparam \
API Swagger Documentation: \
http://localhost:8081/docs/swagger-ui/index.html
