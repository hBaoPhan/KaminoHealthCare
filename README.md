# Kamino Health Care

Đây là phần mềm quản lý hệ thống hiệu thuốc, được xây dựng bằng ngôn ngữ Java với giao diện người dùng desktop (Java Swing).

## 💻 Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 17 / 23)
- **Giao diện (GUI):** Java Swing với giao diện hiện đại, thân thiện.
- **Cơ sở dữ liệu:** Microsoft SQL Server (`mssql-jdbc`).
- **Quản lý thư viện (Build Tool):** Maven.
- **Các thư viện bổ sung:** 
  - `JFreeChart`: Vẽ biểu đồ báo cáo, thống kê.
  - `jBCrypt`: Mã hóa mật khẩu bảo mật.
  - `LGoodDatePicker` / `JCalendar`: Xử lý giao diện chọn ngày tháng.

## 📂 Cấu trúc dự án
Dự án được tổ chức gọn gàng nhằm tách biệt logic xử lý và giao diện:
- `com.example.gui.screens`: Giao diện ứng dụng (các Panel, Component).
- `com.example.dao`: Data Access Object, phụ trách tương tác với cơ sở dữ liệu.
- `com.example.service`: Tầng xử lý logic nghiệp vụ.

## 🎯 Mục đích dự án
Dự án được xây dựng chủ yếu với **mục đích học tập**, nhằm thực hành và áp dụng các kiến thức về lập trình hướng đối tượng (OOP) bằng Java, thiết kế giao diện Swing và thao tác cơ sở dữ liệu SQL Server.
