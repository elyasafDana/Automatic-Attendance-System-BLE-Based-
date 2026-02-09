# Automatic-Attendance-System-BLE-Based-

# Automatic Attendance System (BLE-Based)

An automatic attendance registration system for classrooms, based on **Bluetooth Low Energy (BLE)**.  
The system allows lecturers to register attendance with a single button press, while students in the classroom are automatically identified and verified using their smartphones.

No external hardware.  
No QR codes.  
No manual search.  
Just phones.

---

## 🚀 Overview

Attendance registration is often time-consuming and vulnerable to cheating (e.g. QR code sharing).  
This project solves that problem by using **BLE proximity detection** and **password-based authentication** between lecturer and students.

The system works automatically and scales efficiently, regardless of the number of students in the classroom.

---

## 🧠 How It Works

1. The lecturer opens the app and presses a **Start Attendance** button.
2. The lecturer’s phone starts **broadcasting a secure password via BLE**.
3. Student devices in the physical vicinity:
   - Automatically detect the lecturer’s device (no manual pairing).
   - Receive the broadcasted password.
4. Each student app sends the password to the backend server.
5. The backend verifies the password.
6. Upon successful verification, the student is marked **Present** in the database.

✔ Attendance is only possible when physically present  
✔ No QR codes that can be shared remotely  
✔ No dependency on classroom equipment  

---

## 🛠 Tech Stack & Concepts

- **Mobile Application Development**
- **Bluetooth Low Energy (BLE)**
- **Client–Server Communication**
- **Database Design & Attendance Management**
- **MVVM Architecture**
- **Permissions Handling (Bluetooth, Location, Background Access)**
- **Asynchronous Operations**
- **Debugging & Problem Solving**

---

## 🧩 Architecture

- **MVVM (Model–View–ViewModel)** pattern for clean separation of concerns
- BLE used for:
  - Device discovery
  - Secure short-range communication
- Backend responsible for:
  - Password validation
  - Attendance registration
  - Data persistence

---

## 🔐 Security Considerations

- Attendance is based on **physical proximity**
- Passwords are validated against a backend database
- No static codes (e.g. QR) that can be reused or shared
- BLE range limits remote abuse

---

## 📚 What I Learned

- Designing and managing a real-world database
- Working with BLE and wireless communication
- Handling mobile permissions correctly
- Implementing MVVM in a production-like project
- Debugging complex asynchronous flows
- Thinking about scalability and abuse prevention

---

## 🔮 Future Improvements

- Lecturer dashboard & analytics
- Session history and exports
- Additional authentication layers
- Cross-platform support
- Performance optimizations for large classrooms

---

## 📌 Status

This project was built as a learning-focused, end-to-end system and served as a foundation for future projects.

More improvements and new projects coming soon 🚀

## 📌 you are welcome to watch a video of the system
https://www.youtube.com/watch?v=I0a2OoevXCk
