package com.example.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TroGiupPanel extends JPanel {

    public TroGiupPanel() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false); 
        editorPane.setContentType("text/html"); 

        String htmlContent = getHelpContent();
        editorPane.setText(htmlContent);

        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private String getHelpContent() {
        return "<html>"
            + "<head>"
            + "<style>"
            + "body { font-family: Arial, sans-serif; padding: 20px 30px; color: #333; line-height: 1.6; }"
            + "h1 { color: #0056b3; text-align: center; border-bottom: 2px solid #0056b3; padding-bottom: 10px; margin-bottom: 30px; }"
            + "h2 { color: #d9534f; margin-top: 40px; border-bottom: 1px solid #ccc; padding-bottom: 5px; text-transform: uppercase; }"
            + "h3 { color: #0056b3; margin-top: 25px; font-size: 16px; }"
            + "h4 { color: #28a745; margin-top: 15px; margin-bottom: 5px; font-size: 14px; }"
            + "p { margin-bottom: 10px; text-align: justify; }"
            + "ul { margin-top: 5px; padding-left: 20px; margin-bottom: 15px; }"
            + "li { margin-bottom: 8px; }"
            + "b { color: #000; }"
            + ".note-box { background-color: #f8f9fa; padding: 15px; border-left: 5px solid #0056b3; margin: 15px 0; font-size: 13px; }"
            + ".warning-box { background-color: #fff3cd; padding: 15px; border-left: 5px solid #ffc107; margin: 15px 0; font-size: 13px; }"
            + ".faq-q { font-weight: bold; color: #d9534f; margin-top: 20px; }"
            + ".faq-a { margin-top: 5px; margin-bottom: 20px; padding-left: 15px; border-left: 2px solid #d9534f; }"
            + "</style>"
            + "</head>"
            + "<body>"
            
            + "<h1>TÀI LIỆU HƯỚNG DẪN SỬ DỤNG KAMINO HEALTHCARE</h1>"

            // ================= I. TỔNG QUAN HỆ THỐNG =================
            + "<h2>I. TỔNG QUAN HỆ THỐNG</h2>"
            
            + "<h3>1. Giới thiệu hệ thống Kamino HealthCare</h3>"
            + "<p><b>Mục đích của phần mềm:</b><br>"
            + "Hệ thống Kamino HealthCare được xây dựng nhằm mục đích số hóa và tự động hóa toàn bộ quy trình vận hành của nhà thuốc đạt chuẩn GPP (Good Pharmacy Practice). Phần mềm giúp giải quyết triệt để những bất cập của phương pháp ghi chép sổ sách thủ công thông qua các tính năng cốt lõi:</p>"
            + "<ul>"
            + "<li>Số hóa toàn bộ danh mục sản phẩm (thuốc kê đơn - ETC, thuốc không kê đơn - OTC, thực phẩm chức năng và vật tư y tế) cùng quy trình bán hàng bằng công nghệ quét mã vạch.</li>"
            + "<li>Tự động hóa công tác quản lý kho bãi theo số lô và hạn sử dụng (Date), đồng thời cung cấp các cảnh báo thông minh về tồn kho và hàng sắp hết hạn.</li>"
            + "<li>Quản lý chính xác dòng tiền, lịch sử giao dịch và tự động kết xuất các báo cáo tài chính minh bạch.</li>"
            + "</ul>"
            + "<p><b>Phân quyền người dùng:</b> Để đảm bảo tính bảo mật và toàn vẹn dữ liệu, phần mềm phân chia rõ ràng trách nhiệm và quyền hạn thành 2 nhóm người dùng chuyên biệt:</p>"
            + "<ul>"
            + "<li><b>Nhân viên Quản lý (Chủ nhà thuốc):</b> Là người nắm quyền kiểm soát cao nhất trên hệ thống. Cấp quản lý có toàn quyền sử dụng mọi chức năng, chịu trách nhiệm thiết lập danh mục sản phẩm, tạo các chương trình khuyến mãi, theo dõi báo cáo doanh thu, lợi nhuận và giám sát toàn bộ hoạt động của nhân viên.</li>"
            + "<li><b>Dược sĩ (Nhân viên đứng quầy):</b> Là đội ngũ trực tiếp tương tác với phần mềm hàng ngày. Chức năng chính bao gồm: tiếp đón khách hàng, tư vấn, thiết lập hóa đơn bán hàng, và thực hiện các quy trình đổi/trả hàng hợp lệ.</li>"
            + "<li><b>Giới hạn quyền hạn:</b> Dược sĩ không được phép truy cập vào các module thống kê báo cáo doanh thu tổng hợp hay xem dữ liệu lợi nhuận. Dược sĩ cũng không có quyền chỉnh sửa thông tin danh mục sản phẩm, giá nhập hay giá niêm yết trên hệ thống.</li>"
            + "</ul>"

            + "<h3>2. Hướng dẫn khởi động và đăng nhập</h3>"
            + "<p><b>Cách mở phần mềm:</b></p>"
            + "<ul>"
            + "<li><b>Bước 1:</b> Tại màn hình chính (Desktop) của máy tính quầy thu ngân, tìm biểu tượng (icon) có tên Kamino HealthCare.</li>"
            + "<li><b>Bước 2:</b> Nhấp đúp chuột trái (double-click) vào biểu tượng để khởi động phần mềm. Chờ vài giây để ứng dụng thiết lập kết nối an toàn với máy chủ dữ liệu.</li>"
            + "</ul>"
            + "<p><b>Giao diện đăng nhập:</b> Màn hình đăng nhập sẽ xuất hiện yêu cầu bạn xác thực danh tính để vào hệ thống:</p>"
            + "<ul>"
            + "<li><b>Tên đăng nhập:</b> Nhập tên tài khoản do cấp Quản lý cấp (thường trùng với Mã nhân viên của bạn).</li>"
            + "<li><b>Mật khẩu:</b> Nhập mật khẩu cá nhân. (Ký tự sẽ được ẩn dưới dạng dấu * để bảo mật).</li>"
            + "<li>Sau khi nhập đủ thông tin, nhấn phím Enter trên bàn phím hoặc dùng chuột nhấn vào nút <b>Đăng nhập</b>.</li>"
            + "</ul>"

            + "<div class='warning-box'>"
            + "<b>LƯU Ý QUAN TRỌNG: QUY TRÌNH MỞ CA LÀM VIỆC</b><br>"
            + "Ngay sau khi đăng nhập thành công vào đầu ngày hoặc đầu ca làm việc, hệ thống sẽ yêu cầu bạn thực hiện một bước kiểm soát tài chính bắt buộc: Mở ca làm việc.<br>"
            + "<b>- Thao tác:</b> Bạn cần mở két sắt, đếm tổng số tiền mặt (tiền lẻ thối, tiền chẵn) hiện đang có sẵn trong két. Sau đó, nhập chính xác con số này vào ô \"Tiền mở ca\" trên phần mềm.<br>"
            + "<b>- Tính bắt buộc:</b> Dược sĩ không thể truy cập vào màn hình tạo hóa đơn bán hàng hay thực hiện bất kỳ giao dịch đổi/trả nào nếu ca làm việc chưa được mở và số tiền đầu ca chưa được xác nhận.<br>"
            + "<b>- Ý nghĩa:</b> Thao tác này đóng vai trò như một cột mốc để phần mềm ghi nhận dòng tiền ban đầu. Cuối ca làm việc, phần mềm sẽ lấy số tiền mở ca này cộng với các giao dịch phát sinh để đối soát với số \"Tiền kết ca\", giúp đảm bảo tính minh bạch tuyệt đối."
            + "</div>"

            // ================= II. LUỒNG DƯỢC SĨ =================
            + "<h2>II. HƯỚNG DẪN NGHIỆP VỤ CHO DƯỢC SĨ</h2>"
            
            + "<h3>1. Nghiệp vụ bán hàng (Tạo hóa đơn)</h3>"
            + "<p>Giao diện bán hàng là nơi Dược sĩ thực hiện toàn bộ quá trình tư vấn, xuất thuốc và thu tiền của khách hàng. Để đảm bảo tính chính xác và tối ưu thời gian giao dịch, hãy thực hiện theo đúng các bước sau:</p>"
            + "<h4>Bước 1: Tìm kiếm và Thêm sản phẩm vào giỏ hàng</h4>"
            + "<ul>"
            + "<li><b>Cách 1 (Sử dụng máy quét Barcode):</b> Đặt con trỏ chuột vào ô \"Tìm Mã/Tên sản phẩm\". Dùng máy quét mã vạch quét qua mã in trên bao bì hoặc vỉ thuốc. Hệ thống sẽ tự động nhận diện và thêm sản phẩm đó vào danh sách giỏ hàng bên trái.</li>"
            + "<li><b>Cách 2 (Nhập thủ công từ bàn phím):</b> Gõ mã sản phẩm hoặc tên thuốc vào ô tìm kiếm. Một danh bạ gợi ý tự động sẽ thả xuống ngay dưới thanh tìm kiếm hiển thị danh sách các thuốc trùng khớp. Dùng chuột click vào sản phẩm phù hợp.</li>"
            + "</ul>"
            + "<h4>Bước 2: Xử lý Đơn vị quy đổi linh hoạt</h4>"
            + "<ul>"
            + "<li>Khi thuốc được thêm vào giỏ hàng, hệ thống sẽ mặc định hiển thị đơn vị quy đổi đầu tiên (ví dụ: Hộp).</li>"
            + "<li>Nếu khách hàng muốn mua lẻ, Dược sĩ click đúp chuột trái vào ô \"Đơn vị\" ngay trên dòng sản phẩm đó. Một menu lựa chọn sẽ hiển thị cho phép đổi sang Viên, Vỉ, Tuýp, Chai, hoặc Cái.</li>"
            + "<li><b>Hệ thống tự động hóa:</b> Ngay khi đổi đơn vị, phần mềm sẽ tự động nhân với hệ số quy đổi để cập nhật lại Đơn giá và Thành tiền. Khi thanh toán thành công, hệ thống tự quy đổi ra đơn vị cơ bản nhỏ nhất để trừ tồn kho.</li>"
            + "</ul>"
            + "<h4>Bước 3: Quy trình bắt buộc khi bán Thuốc kê đơn (ETC)</h4>"
            + "<ul>"
            + "<li>Trong quá trình thêm sản phẩm, nếu hệ thống nhận diện sản phẩm thuộc nhóm thuốc kê đơn (ETC), phần mềm sẽ phát ra thông báo nhắc nhở và tự động kích hoạt hộp lựa chọn \"Đơn thuốc\" ở thanh công cụ bên phải.</li>"
            + "<li><b>Thao tác bắt buộc:</b> Dược sĩ bắt buộc phải click vào ComboBox \"Đơn thuốc\" và chọn đúng mã đơn thuốc kèm tên Bác sĩ chỉ định tương ứng với tờ đơn khách hàng mang đến.</li>"
            + "<li><b>Lưu ý bảo mật:</b> Nếu hóa đơn có chứa thuốc ETC mà Dược sĩ không chọn đơn thuốc đi kèm, nút \"THANH TOÁN\" sẽ bị khóa hoặc hệ thống sẽ chặn lại và đưa ra cảnh báo từ chối hoàn tất giao dịch.</li>"
            + "</ul>"
            + "<h4>Bước 4: Cơ chế tự động áp dụng Khuyến mãi (Voucher)</h4>"
            + "<ul>"
            + "<li>Dược sĩ không cần phải ghi nhớ các chương trình ưu đãi. Hệ thống Kamino HealthCare sở hữu tính năng <b>Tự động quét ưu đãi tốt nhất</b>.</li>"
            + "<li>Nếu đạt giá trị tối thiểu của chương trình khuyến mãi, hệ thống sẽ tự động chọn mã giảm giá giúp khách tiết kiệm nhiều tiền nhất.</li>"
            + "<li><b>Trường hợp tặng kèm hàng:</b> Hệ thống sẽ tự động chèn thêm một dòng sản phẩm quà tặng vào cuối giỏ hàng (chữ màu xanh lá, in nghiêng, Đơn giá 0đ). Dược sĩ không thể tự ý sửa đổi số lượng hay đơn vị của dòng quà tặng này.</li>"
            + "</ul>"
            + "<h4>Bước 5: Thực hiện Thanh toán và tất toán dòng tiền</h4>"
            + "<ul>"
            + "<li><b>Kiểm tra tổng tiền:</b> Xem tổng quát các chi phí tại khung tổng kết ở góc dưới.</li>"
            + "<li><b>Tiền mặt:</b> Nhập số tiền khách đưa vào ô \"Tiền khách đưa\". Hệ thống sẽ tự động tính và hiển thị số tiền thừa cần trả lại.</li>"
            + "<li><b>Chuyển khoản:</b> Click chọn mục Chuyển khoản. Màn hình sẽ hiển thị mã QR động. Dược sĩ hướng dẫn khách quét mã và bấm nút \"Xác nhận thanh toán\" khi tiền đã vào tài khoản.</li>"
            + "<li>Khi bấm \"THANH TOÁN\", hệ thống sẽ chính thức lưu hóa đơn vào cơ sở dữ liệu, in biên lai cho khách và tự động trừ tồn kho theo nguyên tắc hạn dùng gần nhất xuất trước (FEFO).</li>"
            + "</ul>"

            + "<h3>2. Nghiệp vụ đổi hàng</h3>"
            + "<p>Nghiệp vụ đổi hàng được áp dụng khi khách hàng mang sản phẩm đã mua đến đổi lấy sản phẩm khác do nhu cầu cá nhân hoặc do phát hiện sản phẩm bị hư hại (vỏ móp, vỉ rách).</p>"
            + "<h4>Bước 1: Tra cứu và kiểm tra tính hợp lệ của Hóa đơn gốc</h4>"
            + "<ul>"
            + "<li>Quét mã vạch trên hóa đơn gốc hoặc nhập mã hóa đơn vào ô tìm kiếm.</li>"
            + "<li><b>Điều kiện chặn tự động:</b> Phần mềm sẽ từ chối tải dữ liệu và báo lỗi nếu hóa đơn gốc đã mua quá hạn 7 ngày, chưa thanh toán thành công, hoặc đã từng đổi trả. Nếu hợp lệ, danh sách thuốc đã mua sẽ được nạp vào bảng \"Chi tiết hóa đơn gốc\".</li>"
            + "</ul>"
            + "<h4>Bước 2: Thao tác nhập số lượng hàng trả lại và phân loại hàng lỗi</h4>"
            + "<ul>"
            + "<li>Dược sĩ kiểm đếm số lượng hàng khách mang đến, click chọn dòng thuốc tương ứng trên bảng gốc và chỉnh sửa cột \"Số lượng Đổi\".</li>"
            + "<li>Nếu thuốc bị hư hỏng, bắt buộc phải nhập số lượng vào cột \"Số lượng lỗi\". <b>Lưu ý hệ thống:</b> Lượng hàng lỗi này sẽ được lưu vết phân bổ riêng, tuyệt đối không cộng dồn số lượng này ngược lại vào kho bán hàng.</li>"
            + "</ul>"
            + "<h4>Bước 3: Quy tắc đổi ngang đặc biệt 1-1 đối với Thuốc kê đơn (ETC)</h4>"
            + "<ul>"
            + "<li>Để đảm bảo tuân thủ liều lượng đơn thuốc của Bác sĩ, Thuốc ETC không được phép đổi sang loại thuốc khác và không được hoàn tiền.</li>"
            + "<li><b>Cơ chế Auto-Sync:</b> Khi Dược sĩ nhập số lượng cần đổi cho thuốc ETC ở bảng gốc, hệ thống tự động sao chép và chèn một dòng y hệt xuống bảng Đổi mới. Dòng này bị khóa cứng, bắt buộc giao dịch đổi ETC là đổi ngang 1-1 sản phẩm cùng loại.</li>"
            + "</ul>"
            + "<h4>Bước 4: Thêm sản phẩm lấy mới và tính toán tiền chênh lệch (Thuật toán Delta)</h4>"
            + "<ul>"
            + "<li>Đối với sản phẩm OTC, TPCN, Mỹ phẩm, khách có thể chọn mặt hàng mới bằng cách gõ vào ô \"Thêm hàng mới\". Hệ thống liên tục tính toán chênh lệch.</li>"
            + "<li><b>Chênh lệch Âm (< 0):</b> Khách đổi hàng rẻ hơn. Dòng chữ \"Số tiền hoàn trả khách\" hiển thị màu đỏ để Dược sĩ rút tiền mặt từ két trả lại khách.</li>"
            + "<li><b>Chênh lệch Dương (>= 0):</b> Khách phải bù tiền. Dược sĩ nhập số tiền khách đưa và bấm THANH TOÁN để hoàn tất.</li>"
            + "</ul>"

            // PHẦN CẬP NHẬT: NGHIỆP VỤ TRẢ HÀNG
            + "<h3>3. Nghiệp vụ trả hàng</h3>"
            + "<p>Nghiệp vụ trả hàng được thực hiện khi khách hàng mang sản phẩm đã mua đến hoàn trả và muốn nhận lại tiền mặt. Đây là một quy trình nhạy cảm liên quan trực tiếp đến dòng tiền két và tồn kho, do đó Dược sĩ cần thao tác cẩn thận theo đúng trình tự sau:</p>"
            + "<h4>Bước 1: Tra cứu và xác thực Hóa đơn gốc</h4>"
            + "<ul>"
            + "<li>Dược sĩ tiếp nhận yêu cầu và biên lai mua hàng từ khách. Nhập mã biên lai vào ô \"Mã hóa đơn gốc\" (txtMaHoaGoc) ở khu vực tìm kiếm và nhấn Enter.</li>"
            + "<li><b>Kiểm tra tự động:</b> Hệ thống sẽ tiến hành rà soát dữ liệu. Nếu hóa đơn không tồn tại, đã quá thời hạn cho phép (thường là 7 ngày), hoặc hóa đơn này đã từng bị đổi/trả trước đó, phần mềm sẽ hiển thị cảnh báo đỏ và khóa giao dịch để ngăn chặn gian lận.</li>"
            + "<li>Nếu hợp lệ, toàn bộ thông tin khách hàng, thời gian mua và danh sách các sản phẩm thuộc hóa đơn đó sẽ được hiển thị chi tiết xuống bảng dữ liệu bên dưới.</li>"
            + "</ul>"
            + "<h4>Bước 2: Xác định Số lượng trả và Phân loại hàng lỗi</h4>"
            + "<ul>"
            + "<li><b>Trả hàng nguyên vẹn:</b> Tại bảng danh sách sản phẩm gốc, sử dụng nút tăng/giảm (Spinner) tại cột \"Số lượng Trả\" để chọn đúng số lượng khách muốn hoàn lại. Hệ thống đã giới hạn thông minh: bạn không thể nhập số nhỏ hơn 0 hoặc lớn hơn số lượng khách đã thực mua trong quá khứ.</li>"
            + "<li><b>Ghi nhận hàng lỗi:</b> Nếu quá trình kiểm tra phát hiện vỏ hộp bị móp méo, vỉ rách vỏ nhôm hoặc thuốc có dấu hiệu hư hỏng, Dược sĩ bắt buộc phải nhập số lượng đó vào cột \"Số lượng lỗi\".</li>"
            + "</ul>"
            + "<div class='note-box'>"
            + "<b>Nghiệp vụ kho bãi tự động:</b> Việc phân định \"hàng nguyên\" và \"hàng lỗi\" cực kỳ quan trọng. Khi hoàn tất, phần mềm sẽ tự động đem \"hàng nguyên\" cất ngược lại vào đúng mã Lô trước kia đã xuất để tiếp tục bán. Riêng \"hàng lỗi\", hệ thống sẽ lưu vết vào bảng phân bổ nhưng tuyệt đối không cộng dồn vào kho bán hàng, giúp nhà thuốc cách ly hoàn toàn hàng hỏng."
            + "</div>"
            + "<h4>Bước 3: Hệ thống tính toán Tiền hoàn trả</h4>"
            + "<ul>"
            + "<li>Dược sĩ không cần dùng máy tính tay. Phần mềm sẽ tự động tính toán Tổng tiền hoàn trả dựa trên Đơn giá thực tế và Thuế VAT của hóa đơn cũ.</li>"
            + "<li><b>Lưu ý về Khuyến mãi:</b> Nếu hóa đơn gốc của khách hàng có sử dụng Voucher giảm giá (Ví dụ: Giảm 10%), hệ thống sẽ tự động bóc tách và trừ đi phần tỷ lệ khuyến mãi tương ứng trên các món hàng được trả lại, đảm bảo nhà thuốc hoàn trả đúng số tiền thực tế khách đã chi trả, không bị thất thoát quỹ.</li>"
            + "</ul>"
            + "<h4>Bước 4: Hoàn tất giao dịch và Hoàn tiền</h4>"
            + "<ul>"
            + "<li>Dược sĩ thông báo tổng số tiền hoàn trả cuối cùng cho khách hàng (hiển thị tại ô Tiền hoàn trả).</li>"
            + "<li>Viết lý do trả hàng (nếu có) vào ô Ghi chú để Quản lý có thể theo dõi sau này.</li>"
            + "<li>Rút tiền mặt từ két quầy trao cho khách và nhấn nút \"XÁC NHẬN TRẢ HÀNG\" (hoặc \"THANH TOÁN\").</li>"
            + "<li>Hệ thống sẽ sinh ra một Mã hóa đơn trả hàng mới (Ví dụ: HDT2605...), in biên lai thu hồi, đồng thời tự động trừ số tiền này khỏi tổng kết ca làm việc (Tiền hệ thống) của bạn để đối soát cuối ngày.</li>"
            + "</ul>"

            + "<h3>4. Kết ca làm việc</h3>"
            + "<ul>"
            + "<li>Dược sĩ thực hiện kiểm đếm vật lý toàn bộ số tiền mặt đang có mặt bên trong két sắt tại quầy thu ngân. Nhập tổng số tiền đếm được vào trường \"Tiền kết ca\".</li>"
            + "<li><b>Cơ chế đối soát thông minh:</b> Hệ thống sẽ so sánh số tiền vừa nhập với trường \"Tiền hệ thống\" (Tiền mở ca + Giao dịch phát sinh - Hoàn trả).</li>"
            + "<li>Nếu xảy ra chênh lệch (thừa hoặc thiếu tiền mặt so với máy tính), Dược sĩ bắt buộc phải nhập lý do cụ thể vào ô \"Ghi chú\" trước khi bấm chọn nút \"ĐÓNG CA\" để bàn giao quầy.</li>"
            + "</ul>"

            // ================= III. LUỒNG QUẢN LÝ =================
            + "<h2>III. HƯỚNG DẪN NGHIỆP VỤ CHO QUẢN LÝ</h2>"
            
            + "<h3>1. Quản lý Danh mục sản phẩm</h3>"
            + "<h4>Bước 1: Quy trình thêm thuốc/mặt hàng mới</h4>"
            + "<ul>"
            + "<li>Nhấn nút \"Làm mới\" để hệ thống tự động mã hóa Mã sản phẩm.</li>"
            + "<li>Nhập đầy đủ thông tin bắt buộc: Tên sản phẩm, Hoạt chất, Đơn giá cơ bản, Thuế VAT, Mô tả.</li>"
            + "<li>Chỉ định nhóm chính xác tại <i>Loại sản phẩm</i> (ETC, OTC, TPCN, MY_PHAM). Tích chọn <i>Trạng thái kinh doanh</i> và nhấn Thêm.</li>"
            + "</ul>"
            + "<h4>Bước 2: Thiết lập mạng lưới Đơn vị quy đổi và Mã vạch (Barcode)</h4>"
            + "<ul>"
            + "<li>Lựa chọn tên đơn vị phù hợp (VIEN, VI, HOP, TUYP, CHAI, CAI).</li>"
            + "<li>Nhập <b>Hệ số quy đổi</b> tương ứng so với đơn vị cơ bản. (Ví dụ: Đơn vị cơ bản là VIEN hệ số 1, thì VI hệ số là 10).</li>"
            + "<li>Nhập mã số hoặc quét mã vạch vào trường <b>Mã vạch (barcode)</b>. Đây là điều kiện ràng buộc duy nhất giúp hệ thống tìm kiếm siêu tốc tại quầy.</li>"
            + "</ul>"

            + "<h3>2. Quản lý Kho và Lô thuốc</h3>"
            + "<h4>Bước 1: Thao tác nhập Lô hàng mới</h4>"
            + "<ul>"
            + "<li>Nhập mã sản phẩm để hệ thống đối chiếu. Khai báo thông số: Số lô (từ NSX), Số lượng, Giá nhập, và Thiết lập hạn sử dụng qua bộ chọn lịch trực quan.</li>"
            + "<li>Hệ thống tự động sinh ra Mã quản lý lô nội bộ để theo dõi.</li>"
            + "</ul>"
            + "<h4>Bước 2: Cơ chế vận hành xuất kho tự động (FEFO)</h4>"
            + "<ul>"
            + "<li>Hệ thống chạy ngầm thuật toán <b>FEFO (First Expired, First Out)</b>. Phần mềm tự động quét và trừ số lượng tồn ở các lô có ngày hết hạn gần nhất trước.</li>"
            + "<li><b>Hàng rào an toàn:</b> Hệ thống tự động cô lập và khóa tính năng bán lẻ đối với bất kỳ lô thuốc nào có ngày hết hạn còn lại dưới 30 ngày.</li>"
            + "</ul>"
            + "<h4>Bước 3: Sử dụng bảng cảnh báo thông minh</h4>"
            + "<ul>"
            + "<li>Chọn bộ lọc <b>Sắp hết hạn</b>: Trích xuất toàn bộ các lô thuốc nằm trong ngưỡng báo động cần xử lý.</li>"
            + "<li>Chọn bộ lọc <b>Hết hàng</b>: Hiển thị các sản phẩm tồn kho chạm mức 0 để quản lý kịp thời nhập hàng mới.</li>"
            + "</ul>"

            + "<h3>3. Quản lý Chương trình khuyến mãi</h3>"
            + "<h4>Bước 1: Khởi tạo thông tin sự kiện ưu đãi</h4>"
            + "<ul>"
            + "<li>Nhấn nút làm mới, nhập Tên chương trình và thiết lập mốc thời gian bắt đầu, kết thúc.</li>"
            + "</ul>"
            + "<h4>Bước 2: Cài đặt điều kiện và hình thức khuyến mãi</h4>"
            + "<ul>"
            + "<li><b>Điều kiện đơn hàng:</b> Nhập Giá trị đơn hàng tối thiểu. Hệ thống chỉ kích hoạt voucher nếu tổng tiền đạt ngưỡng này.</li>"
            + "<li><b>Cấu hình hình thức:</b> Chọn Chiết khấu theo % (hệ thống tự trừ tiền) hoặc Tặng kèm sản phẩm (chỉ định thuốc, số lượng, đơn vị tặng).</li>"
            + "<li><b>Giới hạn đối tượng:</b> Nếu tích chọn thuộc tính \"Ưu đãi thành viên\", hệ thống chỉ áp dụng tự động khi dược sĩ nhập đúng số điện thoại khách hàng thân thiết.</li>"
            + "</ul>"

            // PHẦN CẬP NHẬT: BÁO CÁO & THỐNG KÊ
            + "<h3>4. Báo cáo & Thống kê</h3>"
            + "<p>Phân hệ Thống kê (ThongKePanel) là công cụ tối cao dành riêng cho cấp Quản lý (Chủ nhà thuốc) nhằm theo dõi toàn diện sức khỏe tài chính, đánh giá hiệu quả kinh doanh của nhân viên và kiểm soát chính xác luồng vận động vật lý của hàng hóa trong hệ thống Kamino HealthCare.</p>"
            
            + "<h4>4.1. Khởi tạo bộ lọc thời gian kiểm toán</h4>"
            + "<p>Để xem số liệu báo cáo, phần mềm cung cấp bộ lọc mốc thời gian linh hoạt giúp trích xuất chính xác dữ liệu theo nhu cầu:</p>"
            + "<ul>"
            + "<li><b>Chọn khoảng thời gian:</b> Tại thanh công cụ phía trên, sử dụng bộ chọn ngày trực quan Từ ngày (datePickerTuNgay) và Đến ngày (datePickerDenNgay) để giới hạn khoảng thời gian cần kiểm tra (ví dụ: xem theo ngày, theo tuần, theo tháng hoặc theo năm).</li>"
            + "<li><b>Kích hoạt dữ liệu:</b> Nhấn nút \"Thống kê\" (btnThongKe). Hệ thống sẽ ngay lập tức thực thi lệnh truy vấn ngầm, quét toàn bộ các hóa đơn có trạng thái đã thanh toán thành công thuộc khoảng thời gian chọn để kết xuất số liệu lên màn hình.</li>"
            + "</ul>"
            
            + "<h4>4.2. Hệ thống thẻ chỉ số tài chính cốt lõi</h4>"
            + "<p>Ngay sau khi bấm Thống kê, khu vực trung tâm giao diện sẽ hiển thị các thẻ tóm tắt (Cards) trực quan giúp Quản lý nắm bắt nhanh các chỉ số dòng tiền:</p>"
            + "<ul>"
            + "<li><b>Tổng hóa đơn (lblTongHoaDon):</b> Hiển thị tổng số lượng hóa đơn bán hàng, đổi hàng và trả hàng đã hoàn tất giao dịch.</li>"
            + "<li><b>Tổng doanh thu (lblTongDoanhThu):</b> Tổng số tiền thực tế nhà thuốc thu về (đã bóc tách và trừ đi phần tiền hoàn trả khách từ các biên lai đổi/trả hàng).</li>"
            + "<li><b>Tổng giá vốn (lblTongGiaVon):</b> Tổng giá trị tiền gốc nhập kho của lượng thuốc đã bán ra trong kỳ, được tính toán tự động dựa trên giá nhập của từng mã Số lô cụ thể.</li>"
            + "<li><b>Tổng lợi nhuận (lblTongLoiNhuan):</b> Được tính bằng công thức: Tổng doanh thu - Tổng giá vốn. Chỉ số này giúp quản lý đánh giá trực tiếp hiệu quả sinh lời của nhà thuốc.</li>"
            + "<li><b>Tổng đổi trả (lblTongTraHang):</b> Ghi nhận tổng số lượt giao dịch khách hàng mang thuốc đến hoàn trả hoặc đổi sang sản phẩm khác, giúp giám sát chất lượng dịch vụ quầy.</li>"
            + "</ul>"
            
            + "<h4>4.3. Hệ thống biểu đồ phân tích trực quan</h4>"
            + "<p>Để hỗ trợ Quản lý có cái nhìn chiến lược về xu hướng kinh doanh, Kamino HealthCare tự động xử lý và biểu diễn số liệu thông qua 3 mô hình đồ thị thông minh:</p>"
            + "<ul>"
            + "<li><b>Biểu đồ đường xu hướng (CustomLineChart):</b> Trực quan hóa diễn biến tăng trưởng của Doanh thu và Lợi nhuận theo từng ngày hoặc từng tháng. Biểu đồ này giúp nhận diện nhanh thời điểm nào trong năm nhà thuốc đạt đỉnh doanh thu hoặc giai đoạn nào dòng tiền bị chững lại.</li>"
            + "<li><b>Biểu đồ cột so sánh (CustomBarChart):</b> Biểu diễn sản lượng bán ra hoặc doanh số cụ thể giữa các mặt hàng, nhóm hàng hoặc so sánh hiệu suất doanh thu mang lại giữa các nhân viên trực ca với nhau.</li>"
            + "<li><b>Biểu đồ tròn tỷ trọng (CustomPieChart):</b> Phản ánh cấu trúc đóng góp doanh thu của các phân loại sản phẩm. Nhìn vào biểu đồ, Quản lý sẽ biết ngay dòng sản phẩm nào đang chiếm tỷ trọng lớn nhất trong nhà thuốc (ví dụ: Thuốc kê đơn ETC, Thuốc không kê đơn OTC, Thực phẩm chức năng TPCN, hay MY_PHAM).</li>"
            + "</ul>"
            
            + "<h4>4.4. Bảng kiểm toán chi tiết và Tính năng in ấn báo cáo văn bản</h4>"
            + "<p><b>Tra cứu bảng số liệu chi tiết:</b> Bên dưới hệ thống biểu đồ là khu vực bảng dữ liệu tổng hợp chi tiết, cho phép Quản lý kiểm toán sâu vào từng đối tượng:</p>"
            + "<ul>"
            + "<li><b>Thống kê sản phẩm (ProductStatItem):</b> Danh sách hiển thị chi tiết mã sản phẩm, tên thuốc, phân nhóm y tế cùng các thông số: số lượng bán ra (slBan), doanh thu thực tế thu về (doanhThu), và giá vốn gốc (giaVon). Giúp quản lý phát hiện nhanh thuốc nào bán chạy để nhập thêm hoặc thuốc nào đang tồn kho quá lâu.</li>"
            + "<li><b>Thống kê khách hàng (CustomerStatItem):</b> Gom nhóm dữ liệu chi tiêu theo từng khách hàng thành viên (maKh, tenKh), hỗ trợ đắc lực cho việc lọc danh sách khách hàng thân thiết để đưa ra chính sách chăm sóc phù hợp.</li>"
            + "</ul>"
            + "<p><b>Quy trình in ấn báo cáo văn bản bản cứng:</b> Để phục vụ công tác lưu trữ biên bản kiểm toán tài chính nội bộ hoặc báo cáo cơ quan chức năng, Quản lý thực hiện thao tác in ấn trực tiếp trên phần mềm:</p>"
            + "<ul>"
            + "<li>Nhấn nút \"In báo cáo\" (btnInBaoCao) trên giao diện.</li>"
            + "<li>Hệ thống sẽ khởi tạo một lệnh in chuyên nghiệp thông qua đối tượng quản lý in ấn PrinterJob và giao diện đồ họa Printable. Phần mềm tự động căn biên lề khổ giấy, thiết lập phông chữ tiêu chuẩn và kết xuất toàn bộ số liệu tài chính cùng bảng biểu chi tiết sang hộp thoại cấu hình máy in hệ thống.</li>"
            + "<li>Lựa chọn máy in văn phòng đang kết nối và nhấn Print để xuất bản báo cáo văn bản bản cứng sạch đẹp, rõ ràng.</li>"
            + "</ul>"

            // ================= IV. TÌNH HUỐNG THƯỜNG GẶP =================
            + "<h2>IV. NHỮNG TÌNH HUỐNG THƯỜNG GẶP</h2>"
            
            + "<div class='faq-q'>Tình huống 1: Tại sao tôi không thể bấm nút \"THANH TOÁN\" hoặc bị phần mềm chặn lại?</div>"
            + "<div class='faq-a'>"
            + "Nguyên nhân 1 - Quên \"Mở ca làm việc\": Phần mềm không cho phép giao dịch phát sinh nếu chưa có người chịu trách nhiệm ca. Xử lý: Vui lòng quay lại màn hình Ca làm việc, thực hiện đếm tiền két và nhấn \"Mở ca\".<br><br>"
            + "Nguyên nhân 2 - Thiếu thông tin kê đơn cho thuốc ETC: Bạn bắt buộc phải chọn \"Đơn thuốc\" ở thanh công cụ bên phải. Nút thanh toán sẽ mở khóa sau khi thông tin được bổ sung."
            + "</div>"

            + "<div class='faq-q'>Tình huống 2: Tại sao tôi tìm trên kệ tủ vẫn thấy còn 2 hộp thuốc, nhưng khi quét mã phần mềm lại báo \"Không đủ số lượng tồn kho khả dụng\"?</div>"
            + "<div class='faq-a'>"
            + "Đây là tính năng Bảo vệ an toàn y tế tự động. Điều đó có nghĩa là 2 hộp thuốc trên tay bạn đã hết hạn sử dụng (hoặc thời hạn còn dưới 30 ngày). Thuật toán kiểm soát Lô đã tự động khóa và cô lập lô hàng này. <b>Xử lý:</b> Dược sĩ tuyệt đối không bán 2 hộp này, lập tức rút khỏi kệ và đưa vào khu vực \"Hàng chờ xử lý/Tiêu hủy\"."
            + "</div>"

            + "<div class='faq-q'>Tình huống 3: Tại sao khách hàng có hóa đơn Đổi hàng với phần chọn thêm trị giá 200.000 VNĐ nhưng phần mềm lại không hiển thị mã Khuyến mãi \"Giảm 10% cho đơn từ 150.000 VNĐ\"?</div>"
            + "<div class='faq-a'>"
            + "Đối với giao dịch Đổi hàng, hệ thống được thiết lập để chỉ tính điều kiện khuyến mãi trên phần \"Tiền mua thêm dôi ra\". <br>"
            + "<b>Ví dụ:</b> Khách trả lại thuốc cũ trị giá 100.000đ và chọn mua thuốc mới 200.000đ. Thuật toán lấy: 200.000đ - 100.000đ = 100.000đ (Tiền dôi ra). Vì phần tiền khách chi thêm chỉ là 100.000đ, chưa đạt ngưỡng 150.000đ tối thiểu, nên mã giảm giá sẽ báo \"(Chưa đạt)\" và không được áp dụng."
            + "</div>"
            
            + "<br><br><center><i>&copy; 2026 - Bản quyền thuộc về Nhóm 4 (DHKHMT19ATT)</i></center>"
            
            + "</body>"
            + "</html>";
    }
}