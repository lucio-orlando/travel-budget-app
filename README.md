# Travel Budget App

A simple **travel-budget-app** built using Spring Boot and Pebble templates. This app helps users manage their travel expenses by organizing them into vacations and categories.

## Features

- **Vacation Management**:  
  Create, edit, and delete vacations to group expenses for specific trips.

- **Expense Management**:  
  Add, edit, and delete expenses within a vacation.

- **Category Management**:  
  Manage categories (e.g., Food, Hotels, Transportation) to organize expenses effectively. Categories can be created, edited, and deleted.

## Prerequisites

- **Java 21** or higher
- **Maven** 3.6+
- **Spring Boot** 3.x
- **Pebble** for templating

## Getting Started

1. **Clone the Repository**
   ```bash
   git clone https://github.com/lucio-orlando/travel-budget-app.git
   cd travel-budget-app
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the App**  
   Open a browser and go to: [http://localhost:8080](http://localhost:8080)

## Project Structure

```
src/main/java/ch/lucio_orlando/travel_budget_app
├── controllers        # REST and web controllers
├── services           # Business logic
├── models             # Entities
├── repositories       # Database interactions

src/main/resources
├── static             # assets folder (css, images etc.)
└── templates          # Pebble templates for the frontend
```

## Endpoints
Coming soon

## Technologies Used

- **Backend**: Spring Boot
- **Frontend**: Pebble Templates
- **Database**: H2 (In-memory for development, can be switched to a persistent DB)
- **Build Tool**: Maven

## Future Enhancements

- Visualization of expenses using charts.
- In-Memory db save and load

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
