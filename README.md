# 📸 Pixora
A modern photo manager platform with scalable architecture designed by two students of SBU.
<img width="1536" height="1024" alt="ChatGPT Image Aug 15, 2026, 09_36_17 AM" src="https://github.com/user-attachments/assets/4bafbaf3-6f67-47e7-bf67-3a1eb3462d0e" />

## ✨ Features

• Photo Management: adding and removing albums with multi_selection mode and adding photos from phone camera.

• Post System: Posting albums and photos for other users.

• Comment System: Leaving comment for users' posts with ability of banning comment for other users.

• User Authentication: Secure login/signup system with biometric authentication and session-based architecture.

• Admin Pannel: Comperehensive backend administration system. 

• Beautiful UI/UX: responsive modern screens with smooth animations and attractive images. 

• Developer mode: for testing application

## 🔧 Technologies

• Architecture: Repository_based architecture

• Frontend: Flutter, Dart

• Backend: Java

• DataBase: txt file

• Data Format: JSON

## Project  Structure



## 🚀 Getting Started

### Requirements:

• Flutter SDK

• Dart SDK

• Android Studion/ VS Code

• IntelliJ IDEA

• Android SDK

### Installation

```bash
git clone http://github.com/DavoodFaezian/AP_Project
cd AP_Projcet
flutter pub get
flutter run
```

## 📁 Project Structure

```text
AP_Project/
├── frontend/             # Flutter Mobile Application (MVVM)
│   └── lib/              # Models, Views, ViewModels & Repositories
│
├── src/                  # Java Backend Source Code
│   ├── main/            # Server Sockets, Models & Admin Panel
│   └── test/            # JUnit Concurrency & Load Tests
│
├── files/                # Stored Application Files & Databases (.txt)
├── images/               # Media & Uploaded User Images
│
├── .gitignore            # Git Ignore Configuration
└── README.md             # Project Documentation

