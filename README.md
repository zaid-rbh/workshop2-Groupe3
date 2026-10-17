# DevSecOps-project (VulnerableShop)

> ## WARNING: THIS APPLICATION IS INTENTIONALLY VULNERABLE
> VulnerableShop contains deliberate security weaknesses for **university teaching**.
> **Never deploy it on a public server, a shared network, or any production environment.**
> Run it only on your own computer, with the fictional data it ships with.
> The server is configured to listen on `127.0.0.1` only. Do not change that.

## 1. Educational purpose

`DevSecOps-project` is a small online shop built with Java and Spring Boot for a university DevSecOps course.
The source code contains realistic insecure coding patterns so that students can later practise **reading,
reviewing and analysing source code** with the security tools introduced by the instructor.

Security analysis tools will be selected and used separately during the course. This project has **not** been
scanned, audited or verified by any security tool, and no scanner is configured in the repository.

## 2. Technologies used

| Area | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Spring Boot 3.5.x, Spring MVC |
| Views | Thymeleaf (no JSP) |
| Persistence | Spring Data JPA, Hibernate |
| Database | H2, stored in the local `data/` folder |
| Security | Spring Security (configured weakly on purpose) |
| Frontend | HTML, CSS, Bootstrap 5 (served locally through WebJars) |
| Build | Maven |

## 3. Main features

**Customers:** register, log in and out, view and edit a profile, browse and search products, view product
details, write reviews, use a shopping cart, place a simulated order, view order history and details,
try a file-upload demonstration page.

**Administrators:** dashboard with statistics, list of users, product management (add, edit, delete), list of all
orders.

## 4. Project structure

```text
DevSecOps-project/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
├── docs/
│   ├── ARCHITECTURE.md
│   └── INSTRUCTOR_NOTES.md      (keep private if you publish the project)
└── src/main/
    ├── java/com/devsecops/project/
    │   ├── DevSecOpsProjectApplication.java
    │   ├── config/       (security, web, sample data, fake secrets)
    │   ├── controller/   (Spring MVC controllers)
    │   ├── service/      (business logic)
    │   ├── repository/   (Spring Data JPA repositories)
    │   ├── model/        (JPA entities)
    │   ├── dto/          (form objects)
    │   └── exception/    (global exception handler)
    └── resources/
        ├── application.properties
        ├── static/       (css, js)
        └── templates/    (Thymeleaf pages, admin pages, fragments)
```

## 5. Prerequisites

* Windows 11
* JDK 21 (for example Eclipse Temurin 21)
* IntelliJ IDEA (Community or Ultimate). IntelliJ includes its own Maven, so a separate Maven install is optional.
* Git (only needed to publish the project)
* A web browser

## 6. Setup on Windows 11

1. Open PowerShell and check Java:

   ```powershell
   java -version
   ```

   The output must mention version `21`. If it does not, install JDK 21 and open a new PowerShell window.

2. Get the project: unzip it, or clone your repository, then go into the folder:

   ```powershell
   cd C:\path\to\DevSecOps-project
   ```

## 7. Open the project in IntelliJ IDEA

1. Start IntelliJ IDEA and choose **Open**.
2. Select the `DevSecOps-project` folder (the one that contains `pom.xml`) and click **OK**.
3. If asked, choose **Trust Project**.
4. IntelliJ detects `pom.xml` and imports it as a Maven project. If it does not, right-click `pom.xml` and choose
   **Add as Maven Project**.

## 8. Configure the JDK and Maven

1. **File > Project Structure > Project** and set **SDK** to a JDK 21 (use **Add SDK > Download JDK** if you have none).
   Set **Language level** to 21.
2. **File > Settings > Build, Execution, Deployment > Build Tools > Maven**:
   * **Maven home path:** `Bundled (Maven 3)` is fine.
   * **JDK for importer:** `Project JDK`.
3. Open the **Maven** tool window and click **Reload All Maven Projects**. Wait until the progress bar at the
   bottom of the window disappears: Maven is downloading dependencies (an internet connection is needed once).

## 9. Start the application

In IntelliJ: open `src/main/java/com/devsecops/project/DevSecOpsProjectApplication.java` and click the green
arrow next to `main`, then **Run 'DevSecOpsProjectApplication'**.

Or from PowerShell, if Maven is installed on your machine:

```powershell
mvn spring-boot:run
```

The application is ready when the console shows `Started DevSecOpsProjectApplication`.

To **stop** it, click the red square in IntelliJ's Run window (or press `Ctrl+C` in PowerShell).
To **restart** it, click the green circular arrow in the Run window.

## 10. Local URL

Open <http://localhost:8080>.

## 11. Database initialization and reset

* The database is an H2 file database stored in the `data/` folder next to `pom.xml`.
* On the first start, `DataInitializer` creates the tables and loads fictional users, products, reviews and orders.
* Data is only loaded when the database is empty, so restarting keeps your changes.

**To reset the database:** stop the application, delete the `data` folder, and start the application again.

```powershell
Remove-Item -Recurse -Force .\data
```

To also remove files sent through the upload page: `Remove-Item -Recurse -Force .\uploads`

## 12. Fictional sample credentials

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `alice` | `alice123` | USER |
| `bob` | `123456` | USER |
| `charlie` | `qwerty` | USER |

All accounts, e-mail addresses, addresses and card numbers are fictional.

## 13. Common errors and solutions

| Problem | Solution |
|---|---|
| `java -version` is not 21 | Install JDK 21 and set it as Project SDK in IntelliJ. |
| `release version 21 not supported` | The IntelliJ Project SDK or Maven JDK is older than 21. Change it (section 8). |
| `Port 8080 was already in use` | Stop the other program, or add `server.port=8081` to `application.properties`. |
| Dependencies are red / not found | Check the internet connection, then **Reload All Maven Projects**. |
| `Database may be already in use` | Another copy of the application is still running. Stop it, then start again. |
| Corrupted data after an interrupted run | Stop the app and delete the `data` folder (section 11). |
| Startup error mentioning `requestMatchers` and several servlets | Set `spring.h2.console.enabled=false` in `application.properties`. |
| Browser shows an error page with a stack trace | This is expected for some invalid requests: error output is intentionally verbose. |

## 14. Publishing to GitHub

```powershell
git init
git status
git add .
git commit -m "Initial commit: intentionally vulnerable VulnerableShop"
git branch -M main
git remote add origin https://github.com/<your-username>/DevSecOps-project.git
git push -u origin main
```

Create the empty repository on GitHub first. If you publish the project, keep `docs/INSTRUCTOR_NOTES.md` out of the
public repository (for example add `docs/INSTRUCTOR_NOTES.md` to `.gitignore` before `git add .`). Make the repository
description say clearly that the project is intentionally vulnerable.

## 15. License

MIT. See `LICENSE`.
