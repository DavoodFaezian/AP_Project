# 📸 Pixora
A modern photo manager platform with scalable architecture designed by two students of SBU.
<img width="1536" height="1024" alt="ChatGPT Image Aug 15, 2026, 09_36_17 AM" src="https://github.com/user-attachments/assets/4bafbaf3-6f67-47e7-bf67-3a1eb3462d0e" />
## Multi-User Test
<figure style="margin-bottom: 40px;">
  <img width="1148" height="1220" alt="image" src="https://github.com/user-attachments/assets/3e3257c3-cd7f-48a7-94f1-42bb659e3a7c" />


</figure>

## Test results

<figure style="margin-bottom: 40px;">
  <img width="1920" height="1025" alt="Screenshot 2026-08-15 151743" src="https://github.com/user-attachments/assets/0a4021c6-bbb7-4721-8d72-6681df6a9da4" />
</figure>

<br/>

<figure style="margin-bottom: 40px;">
  <img width="1920" height="1022" alt="Screenshot 2026-08-15 152418" src="https://github.com/user-attachments/assets/0044d9c4-7379-4577-9a8c-3fdf0365fcc1" />
</figure>


The maximum time was 19 seconds and the minimum was 13 seconds (for 1000 users).
The maximum time was 13 seconds and the minimum was 6 seconds (for 500 users).
<br/>
## Conclusion
We put a lot of work into making the app thread-safe and efficient, but sockets ended up being our biggest bottleneck. We also tried storing each user's data in a separate file to boost performance, but benchmarking showed it didn't really make a noticeable difference in speed. Still, one major win from this design is that we can handle as many inactive users as we want without any issues.

## AI usage
The parts that have AI comments are ai generated. Front-end, GenericFileManager test mode, MultiUserTests and Admin CLI are all ai generated.
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

