# Simple Email HTML API

A lightweight REST API for sending styled HTML or plain text emails using **Spring Boot**, **Thymeleaf**, and **Docker**.

---

## Features

- Send plain text emails
- Send HTML emails with dynamic data via Thymeleaf templates
- Send emails with file attachments (multipart/form-data)
- Webhook endpoint for quick plain-text email triggers
- Dockerized for easy deployment
- API key authentication
- Templating system via mounted `/templates` directory
- OpenAPI (Swagger) documentation at `/swagger-ui/index.html`

---

## How It Works

The API uses **Thymeleaf** to render email templates. Place `.html` files in the configured template directory and reference them by filename (without the `.html` extension). Dynamic values are supplied through the `data` field in the request body.

### Template Location

Place your HTML template files in the `/templates/` directory (mounted via Docker volume). The template directory is configured via the `API_TEMPLATE_DIR` environment variable.

**Example template (`welcome.html`):**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
  <h1>Hello, <span th:text="${name}">User</span>!</h1>
  <p>Click <a th:href="${link}">here</a> to get started.</p>
</body>
</html>
```

---

## API Reference

All endpoints require authentication via the `X-API-KEY` request header.
Interactive documentation is available at **`/swagger-ui/index.html`** once the service is running.

---

### POST `/api/email/` — Send an email

Send a plain-text or HTML-templated email.

**Headers:**
```
Content-Type: application/json
X-API-KEY: your-api-key
```

**Plain-text body example:**
```json
{
  "to": "recipient@example.com",
  "subject": "Hello!",
  "body": "This is a plain text email."
}
```

**HTML template example:**
```json
{
  "to": "recipient@example.com",
  "subject": "Welcome!",
  "template": "welcome",
  "data": {
    "name": "Alice",
    "link": "https://example.com"
  }
}
```

> Either `body` or `template` must be provided. When `template` is used, the `data` map populates Thymeleaf variables. The template is resolved by appending `.html` to the given name.

**Response (200 OK):**
```json
{
  "message": "Email sent successfully",
  "status": "SUCCESS",
  "httpStatus": "OK"
}
```

---

### POST `/api/email/attach` — Send an email with attachments

Send an email with one or more file attachments using `multipart/form-data`.

**Parts:**
- `request` — JSON object with the same fields as the standard send-email endpoint (`Content-Type: application/json`)
- `files` — One or more files to attach (repeat the part for multiple files)

**curl example:**
```bash
curl -X POST http://localhost:8080/api/email/attach \
  -H "X-API-KEY: your-api-key" \
  -F 'request={"to":"recipient@example.com","subject":"Report","body":"Please find the report attached."};type=application/json' \
  -F "files=@/path/to/report.pdf" \
  -F "files=@/path/to/data.csv"
```

---

### POST `/api/email/render` — Preview a rendered template

Returns the rendered HTML of a template + data without sending an email. Useful for debugging templates.

**Headers:**
```
Content-Type: application/json
X-API-KEY: your-api-key
```

**Request body:**
```json
{
  "to": "ignored@example.com",
  "subject": "ignored",
  "template": "welcome",
  "data": {
    "name": "Alice",
    "link": "https://example.com"
  }
}
```

**Response (200 OK):** rendered HTML string

---

### GET `/api/email/send` — Webhook (plain-text, query parameters)

Trigger a plain-text email using query parameters. Authenticate via the `apiKey` query parameter instead of the header.

**Example:**
```
GET /api/email/send?to=recipient@example.com&subject=Hello&body=Test+message&apiKey=your-api-key
```

**Parameters:**

| Parameter | Description                   |
|-----------|-------------------------------|
| `to`      | Recipient email address       |
| `subject` | Email subject                 |
| `body`    | Plain-text email body         |
| `apiKey`  | API key (query param for GET) |

---

## Error Responses

**Validation error (400):**
```json
{
  "message": "Validation has failed on request",
  "errors": {
    "to": "must be a well-formed email address"
  }
}
```

**Application error (400 / 500):**
```json
{
  "error": "EmailSendException",
  "message": "Template file not found: welcome"
}
```

**Unauthorized (401):**
```
Invalid API Key
```

---

## Environment Configuration

Configure the service via environment variables (`.env` file or Docker):

```env
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_username
SPRING_MAIL_PASSWORD=your_password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_SSL_ENABLE=false
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
API_KEY=your-api-key
API_TEMPLATE_DIR=/app/templates
```

---

## Docker Usage

```bash
docker-compose up --build
```

Mount your local `templates/` folder into the container:

```yaml
volumes:
  - ./templates:/app/templates
```

The API will be available at `http://localhost:8080`.
Swagger UI will be available at `http://localhost:8080/swagger-ui/index.html`.

---

## To Do

- [x] Send plain text emails
- [x] Send HTML emails via templates
- [x] Webhook support
- [x] Dockerized build
- [x] API key protection
- [x] Add OpenAPI (Swagger) spec
- [x] File attachments via multipart form
- [x] Full documentation and improved README
- [ ] Add CI/CD pipeline

---

## License

MIT – free to use, modify, and deploy.
