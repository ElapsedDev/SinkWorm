# SinkWorm

**SinkWorm** is a Java-based crawler sinkhole and reconnaissance honeypot designed to capture and analyze automated bot, scanner, and crawler traffic targeting HTTP endpoints.
The project is lightweight, passive by design, and intended for environments where visibility into unsolicited or automated web traffic is needed without interfering with legitimate services.

---

## 🔍 Features

- Exposes realistic HTTP honeypot endpoints
- Captures request metadata (paths, methods, headers, user agents)
- Tracks repeated queries and crawl behavior
- Sends structured alerts via Discord webhooks
- Built with clean and efficient Java code
- Designed to run quietly alongside real services

---

## ⚙️ How It Works

1. SinkWorm listens for incoming HTTP requests on configured routes.


2. Each request is logged and analyzed for crawler-like behavior, including:
 - Queries Endpoint Paths
 - API params and query strings
 - Suspicious or non-browser user agents
 - GET and POST request patterns
 - Connection Count

3. When detection thresholds are met, a structured alert is generated and sent for inspection.

---

## 📌 Planned Features

Future updates may include:

 - [ ] Enhance the FakeLoginModule to simulate more complex login behaviors.
 - [ ] Add a CaptchaModule that serves fake CAPTCHA challenges.
 - [ ] Implement a HoneypotModule that creates hidden fields in forms to trap bots.
 - [ ] Develop a UserAgentModule to analyze and respond based on User-Agent strings.
 - [ ] WebsocketModule to handle and log websocket connections.
 - [ ] Some sort playground where the user can connect and assume it's a real exploit, but it's just logging their attempts.

---

## 📦 Requirements

- Java 8 or higher (Java 11+ recommended for HttpClient support)

---

## 🚀 Getting Started

Clone the repository and build the project:

```bash
git clone https://github.com/elapseddev/sinkworm
cd sinkworm
./gradlew shadowJar