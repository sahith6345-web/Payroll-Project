# Enterprise Full-Stack Payroll & HR Management System

A production-ready Enterprise Payroll & HR Management System built with **React 19**, **TypeScript**, **Vite**, **Tailwind CSS**, **Redux Toolkit**, **Recharts**, **Java 21**, **Spring Boot 3**, **Spring Security (JWT)**, **Spring Data MongoDB**, and **Apache PDFBox**.

---

## Key Features

- 🔐 **Multi-Role Access Control (RBAC)**: Support for `SUPER_ADMIN`, `ADMIN`, `HR`, `PAYROLL_MANAGER`, `MANAGER`, and `EMPLOYEE`.
- 🔑 **Secure Authentication**: JWT Access Tokens, Refresh Token rotation, BCrypt password hashing, and Security Filter Chains.
- 👥 **Employee Directory & Profiles**: Complete management of employee personal info, bank details, tax regimes, and status.
- 🏢 **Department & Designation Hierarchy**: Organization unit tracking and pay scale definitions.
- ⏱️ **Attendance & Shift Management**: Interactive Clock-In / Clock-Out triggers, work hour calculation, and history logs.
- 🌴 **Leave & Holiday Workflows**: Leave application modal, balance breakdown, manager approvals, and holiday calendars.
- 💰 **Automated Payroll Engine**: Batch payroll generation, Gross salary, HRA, DA, PF (12%), ESI (0.75%), Professional Tax, Income Tax slabs, and Net pay calculations.
- 📄 **Payslip PDF Generation**: Apache PDFBox generated salary slip PDFs with instant viewing and download endpoints.
- 💸 **Expense Reimbursements**: Business claim submissions and approval workflows.
- 📊 **Dashboard & Reports**: Recharts visual analytics, headcount distribution, and PDF/Excel exports.
- 🛡️ **Audit Logging**: Comprehensive activity tracking for security actions.

---

## Default Initial Credentials (Auto-Seeded)

| Role | Email | Password |
|---|---|---|
| **SUPER_ADMIN** | `superadmin@payroll.com` | `Admin@12345` |
| **HR MANAGER** | `hr@payroll.com` | `Hr@12345` |
| **EMPLOYEE** | `employee@payroll.com` | `Emp@12345` |

---

## Getting Started

### Backend Setup (Spring Boot + MongoDB)
1. Ensure Java 21 is installed.
2. Ensure MongoDB is running locally on `localhost:27017` or configured via `MONGODB_URI` environment variable.
3. Start backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
4. Backend runs on `http://localhost:8080/api/v1`.
5. Swagger API Documentation is available at `http://localhost:8080/swagger-ui.html`.

### Frontend Setup (React + Vite)
1. Ensure Node.js is installed.
2. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```
3. Start Vite dev server:
   ```bash
   npm run dev
   ```
4. Frontend runs on `http://localhost:5173`.

---

## Architecture Overview

```text
c:/Users/ASUS/OneDrive/Documents/payroll/
├── backend/
│   ├── src/main/java/com/payroll/
│   │   ├── common/       # ApiResponse & Page models
│   │   ├── config/       # Security, Mongo, Swagger, DataInitializer
│   │   ├── controller/   # REST Endpoints for Auth, Employee, Dept, Attendance, Leave, Payroll, etc.
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── entity/       # MongoDB @Document Entities
│   │   ├── pdf/          # Apache PDFBox Payslip Generator
│   │   ├── repository/   # Spring Data Repositories
│   │   ├── security/     # JWT Token Provider & Auth Filters
│   │   └── service/      # Business logic services
│   └── src/main/resources/application.yml
│
└── frontend/
    ├── src/
    │   ├── components/   # UI Library (Button, Input, Card, Table, Modal, Badge) & Layouts
    │   ├── contexts/     # Theme & Auth contexts
    │   ├── pages/        # Login, Dashboard, Employees, Department, Attendance, Leave, Payroll, Reports, Settings
    │   ├── routes/       # App routing & ProtectedRoute guarding
    │   ├── services/     # Axios client with JWT interceptors
    │   ├── store/        # Redux Toolkit Slices (auth, theme)
    │   └── types/        # TypeScript interfaces
    └── index.html
```
