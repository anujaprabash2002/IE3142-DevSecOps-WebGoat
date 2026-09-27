# STRIDE Threat Model – OWASP WebGoat

## 1. Threat Modelling Approach

The OWASP WebGoat system was analysed using the STRIDE threat modelling
methodology. The analysis focuses on the main components of the system:
the user web browser, the WebGoat application, the WebWolf application,
the embedded HSQLDB database, and the Docker environment.

The purpose of the threat model is to identify realistic security threats
that may affect the application and to associate each threat with an
appropriate security control.

---

## 2. System Components

### 2.1 User / Web Browser

The user interacts with WebGoat and WebWolf through a web browser.
WebGoat is accessed through port 8080 and WebWolf through port 9090.

### 2.2 WebGoat Application

WebGoat is the main vulnerable web application used for security training.
It is implemented using Java and Spring Boot and runs inside the Docker
environment.

### 2.3 WebWolf Application

WebWolf is a supporting application used with WebGoat security exercises.
It provides attacker-side functionality such as request handling and
supporting security lessons.

### 2.4 HSQLDB

WebGoat uses an embedded HSQLDB database for application data storage.
The database is accessed by the WebGoat application using JDBC.

### 2.5 Docker Environment

The WebGoat environment is deployed using Docker. Docker provides the
isolated runtime environment in which WebGoat and WebWolf operate.

---

## 3. Trust Boundaries

### Trust Boundary 1 – User / Browser to Application

The browser communicates with the Docker-hosted WebGoat and WebWolf
applications through HTTP.

- WebGoat: Port 8080
- WebWolf: Port 9090

This boundary is important because user-controlled requests enter the
application through these interfaces.

### Trust Boundary 2 – Application to Database

WebGoat communicates with the embedded HSQLDB database through a JDBC
connection.

This boundary is important because application data is stored and
retrieved through the database layer.

---

## 4. STRIDE Threats

### Threat 1 – Authentication Spoofing

*STRIDE Category:* Spoofing

An attacker may attempt to impersonate a legitimate WebGoat user in order
to access functionality or information intended for that user.

*Affected Component:* WebGoat

*Security Control:* Strong authentication and secure session management.

---

### Threat 2 – Malicious Input Tampering

*STRIDE Category:* Tampering

An attacker may submit specially crafted input to manipulate application
processing or backend operations.

*Affected Component:* WebGoat

*Security Control:* Server-side input validation and secure coding
practices.

---

### Threat 3 – Information Disclosure

*STRIDE Category:* Information Disclosure

An attacker may exploit an application weakness or insufficient access
control to obtain information that should not be available to the
attacker.

*Affected Components:* WebGoat and HSQLDB

*Security Control:* Authorization, access control and secure data
handling.

---

### Threat 4 – Privilege Escalation

*STRIDE Category:* Elevation of Privilege

A lower-privileged user may attempt to access functionality or resources
that should only be available to a higher-privileged user.

*Affected Component:* WebGoat

*Security Control:* Server-side authorization and access-control
enforcement.

---

## 5. Risk Assessment

A 3 × 3 risk matrix is used for the initial risk assessment.

### Likelihood

- 1 = Low
- 2 = Medium
- 3 = High

### Impact

- 1 = Low
- 2 = Medium
- 3 = High

### Risk Score

Risk Score = Likelihood × Impact

---

## 6. Threat-to-Control Mapping

| Threat | STRIDE Category | Likelihood | Impact | Risk | Security Control |
|---|---|---:|---:|---:|---|
| Authentication Spoofing | S – Spoofing | TBD | TBD | TBD | Authentication and session management |
| Malicious Input Tampering | T – Tampering | TBD | TBD | TBD | Input validation and secure coding |
| Information Disclosure | I – Information Disclosure | TBD | TBD | TBD | Authorization and access control |
| Privilege Escalation | E – Elevation of Privilege | TBD | TBD | TBD | Server-side authorization |

---

## 7. Risk Justification

The likelihood and impact values will be assigned after analysing the
specific WebGoat security scenarios and attack demonstrations. The final
ratings will be justified based on the realistic attack scenario, affected
component and possible security impact.

---

## 8. Threat-to-Control Implementation Mapping

The final implementation location of each control will be documented after
the corresponding WebGoat vulnerabilities and secure coding fixes have
been analysed.

This mapping will identify whether the control is implemented in the
application code, configuration, Docker environment, or CI/CD security
pipeline.