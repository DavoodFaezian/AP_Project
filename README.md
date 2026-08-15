# 📸 Pixora
A modern photo manager platform with scalable architecture designed by two students of SBU.
<img width="1536" height="1024" alt="ChatGPT Image Aug 15, 2026, 09_36_17 AM" src="https://github.com/user-attachments/assets/4bafbaf3-6f67-47e7-bf67-3a1eb3462d0e" />
## Multi-User Test
<br/>
<figure style="margin-bottom: 40px;">
  <img width="1149" height="1369" alt="ChatGPT Image Aug 15, 2026, 02_53_09 PM" src="https://github.com/user-attachments/assets/588407a1-6400-475b-b751-3557fe10edd1" />
</figure>

## Test results
<br/>

<figure style="margin-bottom: 40px;">
  <img width="1920" height="1025" alt="Screenshot 2026-08-15 151743" src="https://github.com/user-attachments/assets/0a4021c6-bbb7-4721-8d72-6681df6a9da4" />
</figure>

<br/>

<figure style="margin-bottom: 40px;">
  <img width="1920" height="1022" alt="Screenshot 2026-08-15 152418" src="https://github.com/user-attachments/assets/0044d9c4-7379-4577-9a8c-3fdf0365fcc1" />
</figure>


The maximum time was 19 seconds and the minimum was 13 seconds (for 1000 users).
The maximum time was 13 seconds and the minimum was 6 seconds (for 500 users).
We tried really hard to make our app thread safe and effiecient for thread safty but our biggest constraint was socket. In our design each user data is save in a separte file and this was for more effeicienty but it realy doesn't have a significant impact on the timing for the tests. but one valuable thing is that we can have as many inactive users as we want
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

