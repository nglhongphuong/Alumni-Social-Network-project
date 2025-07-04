# Alumni-Social-Network

**Final Project – Java Web Development**  
**Timeframe**: April 2025 – June 2025

This project was developed as the final assignment for the *Java Web Development* course. It is a social networking platform for alumni of the university.

---

## 📑 Table of Contents

- [1. Technologies Used](#1-technologies-used)
- [2. Project Topic](#2-project-topic)
- [3. Use Case Diagram](#3-use-case-diagram)
- [4. ERD Diagram](#4-erd-diagram)
- [5. Admin Module Demo](#5-admin-module-demo-spring-mvc--thymeleaf--hibernate)
- [6. Client Module Demo](#6-client-module-demo-reactjs-frontend)
- [7. Installation](#7-installation)
- [Source Code](#-source-code)

---

## 1. Technologies Used

- **Admin Module**:
  - Spring MVC Framework for building features, statistics, and reports
  - Spring Security for authentication and authorization
  - Hibernate for database interaction (Database-First approach)

- **Client Module**:
  - RESTful APIs built using Spring MVC
  - Frontend developed using ReactJS
  - Integrated with Google Mail API to send email notifications
  - Real-time messaging via Firebase Realtime Database
  - Gemini 3.5 integrated as a virtual assistant

- **API Testing**:
  - Postman Collection: [Test APIs here](https://www.postman.com/demo-api-3306/phuong-11-testapi/collection/n62hzyc/social-post)

---

## 2. Project Topic

![Project Topic Image](https://github.com/nglhongphuong/Alumni-Social-Network-project/blob/main/img/%C4%90%E1%BB%81%20t%C3%A0i.png)

---

## 3. Use Case Diagram

![Use Case Diagram](https://github.com/nglhongphuong/Alumni-Social-Network-project/blob/main/img/usecase.png)

---

## 4. ERD Diagram

![ERD Diagram](https://github.com/nglhongphuong/Alumni-Social-Network-project/blob/main/img/ERD.png)

---

## 5. Admin Module Demo (Spring MVC + Thymeleaf + Hibernate)

[![Watch Admin Demo](https://img.youtube.com/vi/fj-yfx3WiE4/hqdefault.jpg)](https://youtu.be/fj-yfx3WiE4?si=tqq54IhnOdYZGuIr)

---

## 6. Client Module Demo (ReactJS Frontend)

[![Watch Client Demo](https://img.youtube.com/vi/JY3LiaNhc7Q/hqdefault.jpg)](https://youtu.be/JY3LiaNhc7Q?si=Eeba_OBlJvk4C4-I)

---

## 7. Installation

1. **Clone dự án về**
2. Mở file `post_db.sql` để tạo database ban đầu (mật khẩu sẽ được cung cấp tại đường dẫn bên dưới)
3. Tài khoản đăng nhập hệ quản trị mặc định:  
   **2251010077phuong@ou.edu.vn / 123456**

4. Mở NetBeans và chạy source code SpringMVC:  
   - Đảm bảo đã đổi mật khẩu database trong `database.properties`  
   - Nhấn nút chạy trong NetBeans  
   _(*Ảnh minh họa sẽ được thêm sau)_

5. Dùng thư mục `postWeb` để chạy ReactJS client:  
   ```bash
   cd postWeb
   yarn install
   yarn start
   ```

---

## 🔗 Source Code

GitHub Repository: [https://github.com/nglhongphuong/Alumni-Social-Network-project](https://github.com/nglhongphuong/Alumni-Social-Network-project)
