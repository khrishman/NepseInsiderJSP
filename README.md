# NepseInsider JSP — Phase 1 (MVP)

A Java web application for Nepal Stock Exchange (NEPSE) stock tracking,
built with **JSP, Servlets, MySQL** following strict **MVC architecture**.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 17                             |
| Server       | Apache Tomcat 11                    |
| View         | JSP + JSTL (inside WEB-INF/pages)   |
| Controller   | Jakarta Servlets (annotations)      |
| Model        | Java POJOs                          |
| Database     | MySQL 8 (JDBC + PreparedStatement)  |
| Styling      | CSS only (no Bootstrap/frameworks)  |
| Build        | Maven                               |
| IDE          | IntelliJ IDEA                       |

---

## Project Structure

```
NepseInsiderJSP/
├── pom.xml
├── schema.sql
├── README.md
└── src/main/
    ├── java/com/nepseinsider/
    │   ├── config/
    │   │   └── DBConfig.java
    │   ├── model/
    │   │   ├── User.java
    │   │   ├── Stock.java
    │   │   ├── Category.java
    │   │   └── Watchlist.java          ← Phase 2 ready
    │   ├── service/
    │   │   ├── UserService.java
    │   │   ├── StockService.java
    │   │   └── CategoryService.java
    │   ├── controllers/
    │   │   ├── HomeServlet.java
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── LogoutServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── StockListServlet.java
    │   │   ├── StockDetailServlet.java
    │   │   ├── ProfileServlet.java
    │   │   ├── AdminDashboardServlet.java
    │   │   ├── AdminStockServlet.java
    │   │   └── AdminUserServlet.java
    │   └── filter/
    │       └── AuthFilter.java
    └── webapp/
        ├── index.jsp
        ├── css/
        │   └── style.css
        └── WEB-INF/
            ├── web.xml
            └── pages/
                ├── includes/
                │   ├── header.jsp
                │   └── footer.jsp
                ├── home.jsp
                ├── login.jsp
                ├── register.jsp
                ├── dashboard.jsp
                ├── stocks.jsp
                ├── stock-detail.jsp
                ├── profile.jsp
                ├── error-404.jsp
                ├── error-500.jsp
                └── admin/
                    ├── dashboard.jsp
                    ├── stock-list.jsp
                    ├── stock-form.jsp
                    └── user-list.jsp
```

---

## Setup Instructions (IntelliJ IDEA + Tomcat 11)

### 1. Prerequisites

- Java 17+ (JDK installed)
- Apache Tomcat 11 (downloaded and extracted)
- MySQL 8 (via XAMPP or standalone)
- IntelliJ IDEA (Ultimate recommended for Tomcat integration)
- Maven (bundled with IntelliJ)

### 2. Database Setup

1. Start MySQL (XAMPP → Start MySQL, or standalone).
2. Open phpMyAdmin or MySQL Workbench.
3. Run the `schema.sql` file to create the database and seed data:
   ```
   mysql -u root -p < schema.sql
   ```
4. Update `DBConfig.java` if your MySQL user/password differs from root / (empty).

### 3. Open Project in IntelliJ

1. **File → Open** → select the `NepseInsiderJSP` folder.
2. IntelliJ will detect `pom.xml` and import as Maven project.
3. Wait for Maven to download dependencies.

### 4. Configure Tomcat 11 in IntelliJ

1. **Run → Edit Configurations → + → Tomcat Server → Local**
2. Set **Application Server** → point to your Tomcat 11 directory.
3. Under **Deployment** tab:
   - Click **+** → **Artifact** → `NepseInsiderJSP:war exploded`
   - Set **Application context** to `/NepseInsiderJSP`
4. Click **Apply → OK**.

### 5. Run

1. Click the green **Run** button (or Shift+F10).
2. Browser opens at: `http://localhost:8080/NepseInsiderJSP/`

---

## Default Login Credentials

| Role  | Username | Password   |
|-------|----------|------------|
| Admin | admin    | admin123   |
| User  | john     | john123    |
| User  | ram      | ram123     |

---

## Phase 1 Features (Completed)

- [x] Complete MySQL database schema (users, categories, stocks, watchlist)
- [x] User registration with duplicate validation (username + email)
- [x] User login with session management
- [x] Role-based access control (Admin / User) via AuthFilter
- [x] User dashboard with stock overview
- [x] Stock listing with search functionality
- [x] Stock detail page
- [x] Profile management (update info + change password)
- [x] Admin dashboard with system stats
- [x] Admin: full stock CRUD (Create, Read, Update, Delete)
- [x] Admin: user management (view, activate, suspend)
- [x] Custom 404 and 500 error pages
- [x] Responsive CSS (flexbox + media queries, no frameworks)
- [x] All JSP files inside WEB-INF/pages (security)
- [x] All DB queries use PreparedStatement (SQL injection safe)

## Phase 2 (Planned)

- [ ] Watchlist feature (schema + model ready)
- [ ] Enhanced sentiment indicators with trends
- [ ] About page and Contact page
- [ ] Password hashing (BCrypt/SHA-256)
- [ ] Account lockout after failed attempts
- [ ] Modern UI polish and animations
- [ ] Additional admin features

---

## MVC Architecture Flow

```
Browser Request
    ↓
Servlet (Controller) — processes request, calls Service
    ↓
Service Layer — business logic, calls DB via JDBC
    ↓
Model (POJO) — data objects
    ↓
JSP (View) — renders response inside WEB-INF/pages
    ↓
Browser Response
```

---

## License

Educational project — CS5003NI Data Structures and Specialist Programming coursework.
