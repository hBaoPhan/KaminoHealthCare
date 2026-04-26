package com.example;
import com.example.dao.HoaDonDAO;
import com.example.entity.HoaDon;
import java.time.LocalDate;
import java.util.List;
public class TestQuery {
    public static void main(String[] args) {
        try {
            HoaDonDAO dao = new HoaDonDAO();
            List<HoaDon> list = dao.timKiem(null, LocalDate.now());
            System.out.println("Hoa don hom nay: " + list.size());
            for (HoaDon hd : list) {
                System.out.println(" - HD: " + hd.getMaHoaDon() + " | ThoiGian: " + hd.getThoiGianTao() + " | ThanhToan: " + hd.isTrangThaiThanhToan() + " | Tien: " + hd.tinhTongTienThanhToan() + " | ChiTiet size: " + (hd.getDsChiTiet() != null ? hd.getDsChiTiet().size() : 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
