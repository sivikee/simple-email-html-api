# A really simple email API

### How does it work?

The api uses thymeleaf to render an email, you can use simple variables inside your template and then attach them to the 'data' object in your request.

### Usage:
- Put template files into the /templates/ folder
- Call the API with '/api/email/'
- You can render the result before sending with using '/api/email/render'
```
{
    "to":"test@gmail.com",
    "subject": "Teszt email!",
    "template":"test.html",
    "data": {
        "name": "sivikee",
        "link": "https://example.com"
    }
}
```

## Todo:
- [x] API now can send simple emails
- [x] API now can send html emails
- [ ] Create docker image and publish to hub
- [ ] Create a better readme
- [ ] Create CI/CD pipeline
- [ ] Add better documentation
- [ ] Add support for file attachments via multipart
