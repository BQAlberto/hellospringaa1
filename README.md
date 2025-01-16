# API Bikes

Este proyecto es una API REST para gestionar órdenes de reparación, talleres, mecánicos y bicicletas. A continuación, se explica cómo configurar y ejecutar el proyecto.

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalados los siguientes requisitos:

- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) o superior
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/) o cualquier base de datos compatible con JPA
- [Postman](https://www.postman.com/) o [Hoppscotch](https://hoppscotch.io/) para pruebas
- [Git](https://git-scm.com/)

## Configuración del Entorno

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/api-bikes.git
   cd api-bikes
   ```

2. Configura las variables de entorno o edita el archivo `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/apibikes
       username: tu_usuario
       password: tu_contraseña
     jpa:
       hibernate:
         ddl-auto: update
   ```

3. Crea la base de datos `apibikes` en tu servidor MySQL:
   ```sql
   CREATE DATABASE apibikes;
   ```

## Construcción y Ejecución

1. Compila y ejecuta el proyecto con Maven:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

2. La API estará disponible en:
   ```
   http://localhost:8080
   ```

## Ejemplos Endpoints Disponibles

| Método | Endpoint                  | Descripción                            |
|--------|---------------------------|----------------------------------------|
| GET    | /workshops                | Lista todos los talleres               |
| POST   | /workshops                | Crea un nuevo taller                   |
| PUT    | /workshops/{workShopId}   | Modifica un taller existente           |
| DELETE | /workshops/{workShopId}   | Elimina un taller                      |
| ...    | ...                       | ...                                    |

Consulta la documentación completa en `docs/endpoints.md`.

## Pruebas

Usa Postman o Hoppscotch para probar los endpoints. Un ejemplo de cuerpo para el endpoint `/repair-orders` (POST):
```json
{
  "bikeId": 43,
  "mechanicId": 1,
  "workShopId": 4,
  "repairDate": "2025-01-15",
  "cost": 150.75,
  "description": "Cambio de frenos y ajuste general"
}
```

## Contribuciones

1. Crea un fork del repositorio.
2. Crea una rama para tu funcionalidad:
   ```bash
   git checkout -b nueva-funcionalidad
   ```
3. Realiza tus cambios y haz un commit:
   ```bash
   git commit -m "Añadida nueva funcionalidad"
   ```
4. Envía un pull request.

## Gestión de Issues

1. Ve a la pestaña **Issues** en tu repositorio en GitHub.
2. Haz clic en **New Issue**.
3. Describe el problema o fallo:
   - **Título:** Breve descripción del problema.
   - **Descripción:** Explica los pasos para reproducir el problema, los resultados esperados y los obtenidos.
   - **Etiquetas:** Añade etiquetas como `bug`, `enhancement`, o `documentation` para clasificar los issues.

### Ejemplo de Issue
```
**Título:** Error al guardar una orden de reparación

**Descripción:**
1. Envía un POST a `/repair-orders` con el siguiente cuerpo:
   ```json
   {
     "bikeId": 999,
     "mechanicId": 1,
     "workShopId": 4,
     "repairDate": "2025-01-15",
     "cost": 150.75,
     "description": "Cambio de frenos y ajuste general"
   }
   ```
2. El sistema responde con un error 500.
3. Se espera que el sistema devuelva un error 404 si el `bikeId` no existe.

**Etiquetas:** bug
