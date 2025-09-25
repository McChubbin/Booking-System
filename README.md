# Cottage Reservation System

A full-stack web application for managing cottage reservations with individual room booking and entire cottage rental options.

## Features

### User Features
- User registration and authentication with JWT
- View available rooms and cottage information
- Book individual rooms or the entire cottage
- Manage reservations (create, view, update, cancel)
- View all active reservations (read-only)
- Email notifications for reservation confirmations, updates, and cancellations

### System Features
- RESTful API design
- Secure user authentication
- Real-time availability checking
- Flexible booking periods (minimum 1 day)
- Conflict prevention for overlapping bookings
- Price calculation based on duration
- Responsive web interface

## Technology Stack

### Backend
- **Java 17** with Spring Boot 3.1.0
- **Spring Security** with JWT authentication
- **Spring Data JPA** for database operations
- **H2 Database** (development) / MySQL (production ready)
- **Spring Mail** for email notifications
- **Maven** for dependency management

### Frontend
- **React 18** with functional components and hooks
- **React Router** for navigation
- **React Bootstrap** for UI components
- **Axios** for API communication
- **Bootstrap 5** for styling

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- npm or yarn
- Maven 3.6+

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build and run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

3. Access the H2 database console (development):
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:cottagedb`
   - Username: `sa`
   - Password: `password`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the React development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User login
- `GET /api/auth/me` - Get current user info

### Rooms
- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/{id}` - Get room by ID
- `GET /api/rooms/available` - Get available rooms (with optional date filters)

### Reservations
- `POST /api/reservations` - Create reservation
- `GET /api/reservations` - Get user's reservations
- `GET /api/reservations/all` - Get all active reservations
- `GET /api/reservations/{id}` - Get reservation by ID
- `PUT /api/reservations/{id}` - Update reservation
- `DELETE /api/reservations/{id}` - Cancel reservation

## Usage

### User Registration and Login
1. Visit `http://localhost:3000/register` to create a new account
2. Login at `http://localhost:3000/login` with your credentials

### Making a Reservation
1. Browse available rooms at `/rooms`
2. Use the date filters to check availability
3. Click "Book Now" to create a reservation
4. Fill in the reservation details and confirm

### Managing Reservations
1. View your reservations at `/my-reservations`
2. Edit or cancel reservations as needed
3. View all active reservations at `/all-reservations`
