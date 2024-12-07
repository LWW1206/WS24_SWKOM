<h1>WS24SWKOM Semesterprojekt</h1>

<h2>Start Application</h2>

The application needs a running Docker Desktop
```bash
docker-compose up
```

To start the application after making changes to the code of the REST Server. 
This will rebuild the paperless rest service. 

<span style="color:rgba(255,0,0,0.7)"> First run **clean** from maven! </span>

```bash
docker-compose up --build
```
Run this command to setup an admin user for the Paperless Server. \
The user is saved in the public db in the auth_user table
```bash
docker compose run --rm webserver createsuperuser
```
<h2>Stop Application</h2>

```bash
docker-compose down
```
This also removes all volumes. After this u have to create the user again with the createsuperuser command. 

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

<h3>Paperless Web UI</h3>

http://localhost/

<h3>Rest server</h3>
Testing connection to Rest Server

http://localhost:8081/hello \
http://localhost:8081/hello/urlparam

API Swagger Documentation: \
http://localhost:8081/docs/swagger-ui/index.html

RabbitMQ: \
http://localhost:15672/
