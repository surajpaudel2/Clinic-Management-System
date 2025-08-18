# Clinic-Management-System
A comprehensive clinic management system built with Spring Boot and MySQL. Features include admin-controlled doctor scheduling, patient appointments, digital consultations, prescriptions, billing with Stripe integration, and role-based access for patients, doctors, receptionists, and admins.

# Clinic Management System

A comprehensive web-based clinic management system built with Spring Boot, MySQL, and modern web technologies.

## Features

### 🏥 Core Functionality
- **Multi-Role System**: Patients, Doctors, Receptionists, Admins, Super Admins
- **Smart Scheduling**: Admin-controlled doctor shifts with automatic time slot generation
- **Appointment Management**: Conflict-free booking with multiple duration options (30-120 minutes)
- **Digital Consultations**: Complete medical records with prescriptions
- **Billing & Payments**: Integrated Stripe payments with multiple payment methods

### 👥 User Roles & Permissions
- **Patients**: Book appointments, view medical records, make payments
- **Doctors**: View schedules, manage consultations, write prescriptions
- **Receptionists**: Front desk operations, appointment management, billing
- **Admins**: Complete system control, shift management, reports
- **Super Admins**: Full system access and configuration

### ⚡ Key Features
- **Flexible Scheduling**: Multiple shifts per day, different shifts for different days
- **Conflict Prevention**: Real-time validation prevents double booking
- **Digital Prescriptions**: Medicine templates for quick prescribing
- **Comprehensive Billing**: Itemized bills with tax calculation
- **Stripe Integration**: Secure online payments with webhook handling

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: MySQL 8.0+ with JSON support
- **Payments**: Stripe API integration
- **Build Tool**: Maven
- **Documentation**: OpenAPI 3.0 (Swagger)

## Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+
- Stripe Account (for payment processing)

### Installation
1. Clone the repository
```bash
git clone https://github.com/surajpaudel2/Clinic-Management-System.git
cd Clinic-Management-System
