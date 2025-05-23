# Travel Budget App

A web application for managing and visualizing travel expenses.

---

## Table of Contents
- [Getting Started](#-getting-started)
  - [Environment Variables](#environment-variables)
  - [Production Deployment](#production-deployment)
  - [Development Environment](#-development-environment)
- [Environment Files](#-environment-files)
- [Useful Commands](#-useful-commands)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## 🚀 **Getting Started**
This is a step-by-step guide to set up the Travel Budget App using Docker and Docker Compose.
Follow these detailed instructions to run the application in a local environment.

### Prerequisites
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)
- Generate [UnSplash API Key](https://unsplash.com/developers) (for image fetching)
- Generate [ExchangeRate API Key](https://www.exchangerate-api.com/) (for currency conversion)

### Cloning the Repository
   ```bash
   git clone https://github.com/lucio-orlando/travel-budget-app.git
   cd travel-budget-app
   ```

###  Environment Variables
- **Create a `.env` file in the root directory of the project.**
- Copy the contents of `.env.example` into your `.env` file.
- Fill in the required values:
  - `UNSPLASH_ACCESS_KEY`: Your Unsplash API key.
  - `EXCHANGE_RATE_API_KEY`: Your ExchangeRate API key.
- Change for the following variables if wanted:
  - `MYSQL_ROOT_PASSWORD`: MySQL root password.
  - `MYSQL_DATABASE`: Name of the database to be created.
  - `MYSQL_USER`: MySQL user.
  - `MYSQL_PASSWORD`: MySQL user password.

### Production Deployment

#### 1. Start the Application

```bash
docker-compose up -d
```

- This will automatically:
    - Start a **MySQL database** with the specified credentials in the `.env` file.
    - Build and run the **Spring Boot application** using the production `.env` configuration.

---

#### 2. Access the Application

- Open your browser and navigate to:  
  [http://localhost:8080](http://localhost:8080)

---

#### 3. Stop the Application

```bash
docker-compose down
```

---

### 🛠 **Development Environment**
The development environment uses a **different** `.env` file for configuration.
- **Create a `.env.development` file in the root directory of the project.**
- Copy the contents of `.env.example` into your `.env.development` file.
- Fill in the required values:
  - `UNSPLASH_ACCESS_KEY`: Your Unsplash API key.
  - `EXCHANGE_RATE_API_KEY`: Your ExchangeRate API key.
- Change for the following variables if wanted:
  - `MYSQL_ROOT_PASSWORD`: MySQL root password.
  - `MYSQL_DATABASE`: Name of the database to be created.
  - `MYSQL_USER`: MySQL user.
  - `MYSQL_PASSWORD`: MySQL user password.

#### 1. Start the Application

```bash
docker-compose --profile development up -d
```

---

#### 2. Access the Application

- Open your browser and navigate to:  
  [http://localhost:8081](http://localhost:8081)

#### 3. Restart the Application to Apply Changes

```bash
docker-compose restart app-dev
```
- Wait for docker to restart.
- Wait for the application to build and start. (no output in the terminal)

---

#### 4. Stop Development Environment

```bash
docker-compose down
```

---

### 📚 **Environment Files**

| File Name          | Purpose              |
|--------------------|----------------------|
| `.env`             | Production Settings (Default) |
| `.env.development` | Development Settings |

---

### 📖 **Useful Commands**

| Action                     | Command                                        |
|----------------------------|------------------------------------------------|
| Start Production           | `docker-compose up -d`                         |
| Start Development (Docker) | `docker-compose restart app-dev` |
| Stop All Services          | `docker-compose down`                          |

## Troubleshooting
### Common Issues

- **Database Connection Errors:**  
  Make sure the `.env` file is correctly configured and the MySQL container is running. (a separate container for the database is created)

- **Port Already in Use:**  
  Ensure no other service is running on port `8080` (production) or `8081` (development).

- **Live Reload Not Working in Development:**  
  Verify that you're running the app using the development profile with the correct `.env.development` file.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
