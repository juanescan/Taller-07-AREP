# Taller-07-AREP

**Integrantes:**

* Juan Cancelado
* Juan José Díaz
* Alejandro Prieto

---

# MicroTwit

Pequeña aplicación tipo *Twitter* (monolito Spring Boot + vistas Thymeleaf + JS) que permite publicar posts (≤ 280 caracteres), leer un stream global, responder, editar y borrar posts.
La aplicación usa **MongoDB** como base de datos y **OAuth2 / AWS Cognito** para autenticación mediante JWT.

---

## Contenido del repositorio

```
/src/main/java/.../twitter          # Código fuente Java (config, controllers, model, repo, service)
src/main/resources/
  ├─ application.properties        # Configuración
  └─ templates/
      ├─ index.html
      └─ muro.html
pom.xml
README.md                           # Este archivo
```

---

## Requisitos previos

* Java 17+
* Maven 3.6+
* MongoDB (local o remoto)
* Cuenta de AWS Cognito (opcional, para autenticación real)
* (Opcional) AWS CLI configurado para despliegue

---

## Configuración (`application.properties`)

Configurar en `src/main/resources/application.properties`:

```properties
# --- Conexión MongoDB ---
spring.data.mongodb.uri=mongodb://localhost:27017/microtwit

# --- Thymeleaf ---
spring.thymeleaf.cache=false

# --- OAuth2 / Cognito ---
spring.security.oauth2.client.registration.cognito.client-id=TU_CLIENT_ID
spring.security.oauth2.client.registration.cognito.client-name=cognito
spring.security.oauth2.client.registration.cognito.provider=cognito
spring.security.oauth2.client.registration.cognito.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.cognito.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

spring.security.oauth2.client.provider.cognito.issuer-uri=https://cognito-idp.us-east-1.amazonaws.com/<USER_POOL_ID>
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.us-east-1.amazonaws.com/<USER_POOL_ID>

app.cognito.domain=https://<your-domain>.auth.us-east-1.amazoncognito.com
app.cognito.client-id=TU_CLIENT_ID
app.cognito.logout-redirect=http://localhost:8080/
```

> Si no configuras Cognito, la aplicación seguirá arrancando, pero las rutas protegidas requerirán autenticación vía OAuth2.

---

## Compilar y ejecutar

1. Levantar MongoDB (por ejemplo, con Docker):

   ```bash
   docker run --name microtwit-mongo -p 27017:27017 -d mongo:6
   ```

2. Compilar el proyecto:

   ```bash
   mvn clean package -DskipTests
   ```

3. Ejecutar el jar:

   ```bash
   java -jar target/twitter-0.0.1-SNAPSHOT.jar
   ```

4. Abrir en el navegador:

   * [http://localhost:8080/](http://localhost:8080/) → Página principal
   * [http://localhost:8080/muro](http://localhost:8080/muro) → Stream global

---

## Endpoints principales

| Método   | Ruta                      | Descripción                                    |
| -------- | ------------------------- | ---------------------------------------------- |
| `GET`    | `/public/hello`           | Endpoint público                               |
| `GET`    | `/private/hello`          | Endpoint protegido (requiere login)            |
| `GET`    | `/token`                  | Devuelve el `id_token` del usuario autenticado |
| `POST`   | `/api/posts`              | Crear un nuevo post                            |
| `GET`    | `/api/posts`              | Listar todos los posts (paginado)              |
| `GET`    | `/api/posts/mine`         | Posts del usuario actual                       |
| `GET`    | `/api/posts/{id}`         | Obtener post por ID                            |
| `GET`    | `/api/posts/{id}/replies` | Listar respuestas                              |
| `PUT`    | `/api/posts/{id}`         | Editar post (solo autor)                       |
| `DELETE` | `/api/posts/{id}`         | Eliminar post (solo autor)                     |

---

## Uso del token JWT

1. Inicia sesión desde el navegador en

   ```
   http://localhost:8080/oauth2/authorization/cognito
   ```

2. Visita

   ```
   http://localhost:8080/token
   ```

   y copia el `id_token`.

3. Usa el token para probar con `curl`:

   ```bash
   TOKEN="PEGA_AQUI_EL_ID_TOKEN"

   # Crear post
   curl -X POST http://localhost:8080/api/posts \
   -H "Authorization: Bearer $TOKEN" \
   -H "Content-Type: application/json" \
   -d '{"content":"Hola desde curl"}'

   # Listar posts
   curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/posts
   ```

---

## Frontend / Interfaz Web

* **index.html** → Página de inicio.
* **muro.html** → Stream global (mostrar y crear posts).
* Las páginas están integradas con el backend usando **Thymeleaf + JS (Fetch API)**.

---

## Pruebas

Ejecutar pruebas automáticas:

```bash
mvn test
```

Pruebas manuales:

1. Levantar la aplicación y MongoDB.
2. Iniciar sesión en Cognito.
3. Publicar, editar y eliminar posts desde `/muro`.
4. Verificar la información en MongoDB.

---

## Despliegue

### Desplegar en AWS Lambda (versión microservicios)

1. Separar módulos: `user-service`, `post-service`, `stream-service`.
2. Empaquetar con AWS SAM o Serverless Framework.
3. Desplegar con:

   ```bash
   sam build && sam deploy --guided
   ```
4. Configurar API Gateway y Cognito como authorizer.

### Desplegar frontend en S3

1. Subir vistas a un bucket público:

   ```bash
   aws s3 mb s3://microtwit-frontend-bucket
   aws s3 sync src/main/resources/templates/ s3://microtwit-frontend-bucket --acl public-read
   ```
2. Habilitar el acceso público o usar CloudFront.

---

## 📚 Documentación Swagger / OpenAPI

El proyecto incluye generación automática de documentación de endpoints con **Springdoc OpenAPI 3** (Swagger UI).

### Dependencia Maven

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### Acceso a la documentación

* **Swagger UI:**
  👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

* **OpenAPI JSON:**
  👉 [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Licencia

Este proyecto está licenciado bajo la **Licencia MIT** — puedes usarlo, modificarlo y distribuirlo libremente con fines académicos o personales.
[Licencia](./LICENSE)