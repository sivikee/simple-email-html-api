# 📧 Simple Email API

A lightweight REST API for sending styled HTML or plain text emails using **Spring Boot**, **Thymeleaf**, and **Docker**.

---

## 🚀 Features

- ✅ Send plain text emails  
- ✅ Send HTML emails with dynamic data (via Thymeleaf)  
- ✅ Simple webhook endpoint for quick email triggers  
- ✅ Dockerized for easy deployment  
- 🔒 API key protection  
- 🔧 Templating system via mounted `/templates` directory  

---

## 📦 How It Works

The API uses **Thymeleaf** to render email templates. You can insert variables in your `.html` templates, and supply their values through the `data` field in your request.

### 📁 Template Location

Place your HTML template files in the `/templates/` directory (mounted via Docker volume).

---

## 📬 Sending an Email

### Endpoint: `POST /api/email/`

```json
{
  "to": "test@gmail.com",
  "subject": "Test email!",
  "template": "test.html",
  "data": {
    "name": "sivikee",
    "link": "https://example.com"
  }
}
```

🧠 The `data` object will be used to populate template variables.

### Required Header:

```
X-API-Key: your-api-key
```

---

## 👀 Preview an Email

### Endpoint: `POST /api/email/render`

Returns the rendered HTML of the provided template + data, without sending the email. Useful for debugging.

---

## 🔁 Webhook Mode

You can trigger simple emails using form parameters:

### Endpoint: `POST /api/email/webhook`

**Parameters:**
- `to`
- `subject`
- `body`

📌 No templating here — just quick plain text emails.

---

## 🛠️ Environment Configuration

Use environment variables (via `.env` or Docker) to configure:

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

## 🐳 Docker Usage

```bash
docker-compose up --build
```

Mount your local `templates/` folder into the container:

```yaml
volumes:
  - ./templates:/app/templates
```

---

## 📌 To Do

- [x] Send plain text emails  
- [x] Send HTML emails via templates  
- [x] Webhook support  
- [x] Dockerized build  
- [ ] Add OpenAPI (Swagger) spec  
- [ ] Improve README and usage examples ✅  
- [ ] Add CI/CD pipeline  
- [ ] Full documentation  
- [ ] File attachments via multipart form  

---

## 📄 License

MIT – free to use, modify, and deploy.
