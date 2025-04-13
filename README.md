# Loon - Salon Booking Mobile Application

Loon is a mobile application designed to simplify salon booking and management. With Loon, users can seamlessly book appointments, and salon owners can efficiently manage their schedules and bookings. Built using **Kotlin** and **Jetpack Compose**, the app leverages modern frameworks and Firebase for robust functionality.


## Features

### For Users:
- **Account Creation**: Sign up and log in securely using Firebase Authentication.
- **Browse Services**: Explore available services, categories, and pricing.
- **Book Appointments**: Select desired services, choose time slots, and confirm bookings.
- **Real-Time Notifications**: Get notified about appointment updates.

### For Salon Owners:
- **Dashboard Management**: Access a dedicated salon dashboard to manage appointments.
- **View Bookings**: See all bookings categorized by status (All, Pending, Approved, Done).
- **Update Booking Status**: Approve or mark appointments as done.
- **Service Management**: Manage your salon’s services and pricing.
- **Insights**: View analytics of completed bookings .


## Firebase Integration

The app uses Firebase for the following:
- **Authentication**: Secure login and registration.
- **Firestore Database**: Store and retrieve bookings, services, and salon data.
- **Realtime Database**: Fetch user details (e.g., first name, last name) for bookings.


## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Firebase Firestore and Realtime Database
- **Authentication**: Firebase Authentication
- **Cloud Functions**: Firebase Cloud Functions
- **Architecture**: MVVM (Model-View-ViewModel)

---


## How to Run

### Prerequisites:
1. Install [Android Studio](https://developer.android.com/studio).
2. **Create a Firebase Account**:
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Create a new project.
   - Enable Firebase Authentication, Firestore Database, and Realtime Database.
3. **Download Firebase Configuration File**:
   - After creating the project, go to the project settings in the Firebase Console.
   - Download the `google-services.json` file.
   - Place the file in the `app/` directory of your Android project.

### Steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/RealChAuLa/Loon.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and install dependencies.
4. **Add Firebase JSON File**:
   - Ensure the `google-services.json` file is placed in the `app/` directory.
5. Run the application on an emulator or physical device.


## Contribution

We welcome contributions! If you'd like to contribute:
1. Fork the repository and make your changes.
2. Create a pull request describing your changes.


## License

This project is licensed under the MIT License

