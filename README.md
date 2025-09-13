<xaiArtifact artifact_id="f0544c6b-74be-485a-ba23-23a237986a6d" artifact_version_id="e8f5a2a1-4c5d-4e9e-9b5f-3f8a1b2c3d4e" title="README.md" contentType="text/markdown">
  
# Mutqin Backend System 

A Spring Boot application for managing interactions among **Students**, **Tutor**, **Admins**, and **Parent**, with **Google OAuth2** for authentication, **JWT** for secure access, and **Calendly** for session scheduling.

## Features
- **Authentication**: Google OAuth2 and email/password login with JWT tokens.
- **Session Management**: Book and manage sessions via Calendly.
- **Progress Tracking**: Monitor student progress (sessions, quizzes, pages learned).
- **Badge Management**: Sheikhs assign/create badges for students.
- **Reporting**: Send reports to parents via WhatsApp API or email.
- **Notifications**: Automated user notifications.
- **Role-Based Access**: Supports Student, Sheikh, Admin, and Parent roles.
- **Secure Token Handling**: Tokens and user details sent in POST request body to `/api/auth/success`.

## Tech Stack
- **Backend**: Spring Boot 3.3.4
- **Security**: Spring Security (OAuth2 Client, JWT)
- **Database**: PostgreSQL hosted on Supabase
- **Integrations**:
  - Google OAuth2: User authentication
  - Calendly: Session scheduling
- **Dependencies**: Spring Data JPA, JJWT, PostgreSQL Driver, Lombok
- **Logging**: SLF4J with Logback

## Setup
1. **Google OAuth2**:
   - Get client ID/secret from [Google Cloud Console](https://console.cloud.google.com).
   - Configure in `application.properties`:
     ```properties
     spring.security.oauth2.client.registration.google.client-id=<your-client-id>
     spring.security.oauth2.client.registration.google.client-secret=<your-client-secret>
     spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
     spring.security.oauth2.client.registration.google.scope=email,profile
     spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
     spring.security.oauth2.client.provider.google.token-uri=https://www.googleapis.com/oauth2/v4/token
     spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
     spring.security.oauth2.client.provider.google.user-name-attribute=sub
     ```

2. **Calendly**:
   - Obtain API token from [Calendly Developer Portal](https://developer.calendly.com).
   - Add to `application.properties`:
     ```properties
     calendly.api.token=<your-calendly-api-token>
     ```

3. **Database (Supabase)**:
   - Create a PostgreSQL project on [Supabase](https://supabase.com).
   - Get database URL, username, and password from Supabase dashboard.
   - Configure in `application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://db.your-project-ref.supabase.co:5432/postgres
     spring.datasource.username=postgres
     spring.datasource.password=<your-supabase-password>
     spring.jpa.hibernate.ddl-auto=update
     ```

4. **JWT**:
   - Set secret and expiration:
     ```properties
     jwt.secret=<your-jwt-secret>
     jwt.expiration=86400000
     ```

5. **Frontend Redirect**:
   - Configure OAuth2 callback:
     ```properties
     app.oauth2.frontend-redirect-uri=http://localhost:3000/auth/callback
     ```

## API Overview
See [API Documentation](api_documentation.md) for details. Key endpoints:
- **Authentication**:
  - `POST /auth/google/login`: Google OAuth login.
  - `POST /auth/google/signup`: Google OAuth registration.
  - Response:
    ```json
    {
      "token": "string",
      "user": {
        "id": "string",
        "role": "string",
        "username": "string"
      }
    }
    ```
- **Student**: Book sessions (`/students/sessions/book`), track progress (`/students/progress`).
- **Sheikh**: Create sessions (`/sheikhs/sessions`), assign badges (`/sheikhs/badges/assign`).
- **Admin**: Manage users (`/admin/students`, `/admin/sheikhs`).
- **Parent**: View reports (`/parents/reports`).

## Security
- **Google OAuth2**: Fetches user info (email, name, Google ID) securely.
- **JWT**: Validates requests via `Authorization: Bearer <token>` header.
- **Secure Tokens**: Sent in POST body to `/api/auth/success`.
- **CORS**: Allows requests from `http://localhost:3000`, `4200`, `5173`.

## Integrations
- **Google OAuth2**: Authenticates users, processes data via `OAuth2Service`, and sends tokens securely.
- **Calendly**: Maps `session_id` to `calendly_event_id` for session creation and booking.
- **Supabase PostgreSQL**: Hosts database with connection pooling and SSL support.

## Project Structure
```
src/
├── main/
│   ├── java/org/example/mutqinbackend/
│   │   ├── config/               # Security and app configs
│   │   ├── controller/           # API endpoints
│   │   ├── entity/               # User entity
│   │   ├── repository/           # JPA repositories
│   │   ├── security/             # JWT, OAuth2, filters
│   │   ├── service/              # OAuth2Service
│   ├── resources/
│   │   └── application.properties
├── api_documentation.md
└── README.md
```

## Notes
- Deploy with **HTTPS** in production.
- Use environment variables for sensitive data.
- Test Calendly API calls with valid `calendly_event_id`.
</xaiArtifact>
