package com.example.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class TroGiupPanel extends JPanel {

    public TroGiupPanel() {
        initComponents();
    }

    private void initComponents() {
        // Thiết lập bố cục cho Panel
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Khởi tạo JEditorPane để hiển thị HTML
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false); // Không cho phép người dùng gõ text vào
        editorPane.setContentType("text/html"); // Thiết lập kiểu nội dung là HTML

        // Lấy nội dung HTML hướng dẫn
        String htmlContent = getHelpContent();
        editorPane.setText(htmlContent);

        // Đưa con trỏ chuột lên đầu trang web sau khi load
        editorPane.setCaretPosition(0);

        // Thêm thanh cuộn
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(scrollPane, BorderLayout.CENTER);
    }

    // Phương thức chứa chuỗi HTML hướng dẫn sử dụng
    private String getHelpContent() {
        return "<html>"
            + "<head>"
            + "<style>"
            + "body { font-family: Arial, sans-serif; padding: 20px; color: #333; }"
            + "h1 { color: #0056b3; text-align: center; border-bottom: 2px solid #0056b3; padding-bottom: 10px; }"
            + "h2 { color: #d9534f; margin-top: 30px; border-bottom: 1px solid #ccc; padding-bottom: 5px; }"
            + "h3 { color: #5bc0de; }"
            + "ul { margin-top: 5px; }"
            + "li { margin-bottom: 10px; line-height: 1.5; }"
            + "b { color: #333; }"
            + ".highlight { background-color: #f9f9f9; padding: 10px; border-left: 4px solid #0056b3; margin-bottom: 20px; }"
            + "</style>"
            + "</head>"
            + "<body>"
            
            + "<h1>HƯỚNG DẪN SỬ DỤNG HỆ THỐNG KAMINO HEALTHCARE</h1>"
            
            + "<div class='highlight'>"
            + "<b>Lưu ý:</b> Hệ thống phân quyền rõ ràng giữa <b>Dược sĩ</b> (nhân viên bán hàng) và <b>Quản lý</b>. Vui lòng xem hướng dẫn theo đúng chức vụ của bạn."
            + "</div>"

            // ================= LUỒNG DƯỢC SĨ =================
            + "<h2>I. DÀNH CHO DƯỢC SĨ (NGHIỆP VỤ BÁN HÀNG)</h2>"
            + "<ul>"
            + "<li><b>1. Quản lý ca làm:</b><br>"
            + " - <i>Mở ca làm:</i> Bắt buộc thực hiện khi bắt đầu ca trực. Hệ thống sẽ ghi nhận số tiền mặt đầu ca có trong két.<br>"
            + " - <i>Đóng ca làm:</i> Thực hiện trước khi kết thúc ca. Bạn cần đếm tiền mặt thực tế và đối chiếu với tiền hệ thống tính toán."
            + "</li>"
            
            + "<li><b>2. Bán hàng (Lập hóa đơn):</b><br>"
            + " - Nhập số điện thoại khách hàng hoặc chọn 'Khách lẻ'.<br>"
            + " - Thêm sản phẩm vào giỏ hàng. Nếu là thuốc kê đơn (ETC), hệ thống sẽ yêu cầu bạn nhập thêm thông tin đơn thuốc (Tên Bác sĩ, Ngày kê, Cơ sở khám).<br>"
            + " - Chọn hình thức thanh toán (Tiền mặt / Chuyển khoản) và nhấn 'Thanh Toán' để in hóa đơn."
            + "</li>"

            + "<li><b>3. Đổi / Trả hàng:</b><br>"
            + " - Chọn chức năng Đổi/Trả hàng. Nhập mã hóa đơn gốc của khách hàng để hệ thống truy xuất dữ liệu.<br>"
            + " - <i>Lưu ý:</i> Chỉ chấp nhận đổi/trả với hóa đơn không quá 07 ngày và không áp dụng cho thuốc ETC."
            + "</li>"

            + "<li><b>4. Quản lý Khách hàng Thành viên:</b><br>"
            + " - Có thể đăng ký hồ sơ mới (Tên, SĐT) cho khách để tích điểm. Có thể tra cứu lịch sử mua hàng của khách qua số điện thoại."
            + "</li>"
            + "</ul>"

            // ================= LUỒNG QUẢN LÝ =================
            + "<h2>II. DÀNH CHO QUẢN LÝ (QUẢN TRỊ HỆ THỐNG)</h2>"
            + "<ul>"
            + "<li><b>1. Quản lý Sản phẩm & Lô hàng:</b><br>"
            + " - <i>Tạo sản phẩm mới:</i> Nhập các thông tin gốc (tên, hoạt chất, phân loại ETC/OTC).<br>"
            + " - <i>Quản lý lô:</i> Khi nhập hàng mới, quản lý cần tạo thông tin Lô (Số lô, hạn sử dụng, giá nhập). Hệ thống sẽ tự động ưu tiên xuất bán lô cận date trước."
            + "</li>"

            + "<li><b>2. Quản lý Khuyến mãi:</b><br>"
            + " - Thiết lập các chương trình ưu đãi (giảm giá % hoặc tặng kèm sản phẩm). Bạn phải cài đặt thời gian bắt đầu, kết thúc và giá trị hóa đơn tối thiểu để áp dụng."
            + "</li>"

            + "<li><b>3. Quản lý Tài khoản & Nhân viên:</b><br>"
            + " - Thiết lập tài khoản, mật khẩu và cấp quyền (Dược sĩ / Quản lý) cho nhân viên mới."
            + "</li>"

            + "<li><b>4. Xem Báo cáo Thống kê:</b><br>"
            + " - <i>Thống kê Doanh thu:</i> Xem biểu đồ doanh thu, lợi nhuận theo ngày, tuần, tháng.<br>"
            + " - <i>Thống kê Sản phẩm:</i> Xem top sản phẩm bán chạy, cảnh báo các lô thuốc sắp hết hạn hoặc tồn kho quá lâu.<br>"
            + " - <i>Thống kê Khách hàng:</i> Xem tổng số khách hàng mới, top khách hàng chi tiêu nhiều nhất.<br>"
            + " - Quản lý có thể ấn nút 'Xuất PDF' để lưu báo cáo về máy."
            + "</li>"
            + "</ul>"
            
            + "<br><br><center><i>&copy; 2026 - Bản quyền thuộc về Nhóm 4 (DHKHMT19ATT)</i></center>"
            
            + "</body>"
            + "</html>";
    }
}
