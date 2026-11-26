# Keeper Tasks

A single-user task manager built with Java 17, Spring Boot, and a plain HTML/CSS/JavaScript frontend. The Spring Boot application serves both the web page and the REST API.

## Requirements

- Java 17
- Maven 3.6.3 or later
- A current desktop or mobile browser

## Run

From the repository root, start the application:

```shell
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

## Build and test

```shell
mvn test
mvn package
```

## Task behavior

- Add a task with a title up to 100 Unicode code points after surrounding Unicode whitespace is trimmed. Empty, whitespace-only, and overlong titles are rejected.
- Tasks are displayed in creation order. Marking or unmarking a task does not change its position.
- All shows every task; Active shows incomplete tasks; Done shows completed tasks. The selected filter resets to All on reload.
- Completed tasks show a checked checkbox and strike-through text.
- The application supports up to 1,000 in-memory tasks. Tasks are lost when the application restarts.
- There is no login; the application is intended for one user and one shared task list.

## REST API

All routes use JSON where a request or response body is shown.

| Method and route | Request | Success |
|---|---|---|
| `GET /api/tasks` | — | `200 OK`, task array in creation order |
| `POST /api/tasks` | `{"title":"Buy milk"}` | `201 Created`, `{"id":1,"title":"Buy milk","done":false}` |
| `PATCH /api/tasks/{id}` | `{"done":true}` or `{"done":false}` | `200 OK`, updated task |
| `DELETE /api/tasks/{id}` | — | `204 No Content` |

Invalid titles return `400 Bad Request`. Unknown task IDs on completion updates and deletion return `404 Not Found`. API errors use this shape:

```json
{
  "error": {
    "code": "INVALID_TITLE",
    "message": "Title must not be empty."
  }
}
```

The `code` identifies the error and `message` describes it. Malformed requests return `400`; unsupported methods on these routes return `405`.

## Accessibility and security

The interface supports keyboard and touch interaction, visible keyboard focus, and viewport widths from 360px through desktop sizes. The latest versions of Chrome, Edge, Firefox, and Safari are supported.

Task titles and error messages are rendered as text, not interpreted as HTML. No authentication or CORS configuration is provided; use the application in a local or otherwise trusted environment.

## Local configuration and secrets

Do not commit passwords, tokens, private keys, or other secret values. `.env`, `.env.*`, key files, PEM files, and `application-local.properties` are ignored by Git. Keep any local configuration in those ignored files and never paste secret values into source files or documentation.
