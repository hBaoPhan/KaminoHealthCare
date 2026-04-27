package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;
import com.example.entity.enums.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    private SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();

    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHD) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String truyVan = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon = ?";
        
        DonViQuyDoiDAO dvqdDAO = new DonViQuyDoiDAO();
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, maHD);
            ResultSet ketQua = lenh.executeQuery();

            while (ketQua.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                
                // Set Hóa Đơn và Đơn Vị Quy Đổi (Ở mức cơ bản lấy mã)
                ct.setHoaDon(new HoaDon(ketQua.getString("maHoaDon")));
                
                // Lưu ý: Cột trong DB của bạn theo sơ đồ là maDonVi (có thể đổi lại thành maDonViQuyDoi nếu bạn tạo bảng như vậy)
                String maDV = ketQua.getString("maDonVi"); 
                ct.setDonViQuyDoi(dvqdDAO.timTheoMa(maDV));
                
                ct.setSoLuong(ketQua.getInt("soLuong"));
                ct.setDonGia(ketQua.getDouble("donGia"));
                ct.setLaQuaTangKem(ketQua.getBoolean("laQuaTangKem"));
                
                // Lấy danh sách Phân bổ lô từ SuPhanBoLoDAO nạp vào Entity
                List<SuPhanBoLo> dsLô = suPhanBoLoDAO.layPhanBoLoCuaChiTiet(maHD, maDV);
                ct.setDsPhanBoLo(dsLô);
                
                danhSach.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean them(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        String truyVan = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, ct.getHoaDon().getMaHoaDon());
            lenh.setString(2, ct.getDonViQuyDoi().getMaDonVi());
            lenh.setInt(3, ct.getSoLuong());
            lenh.setDouble(4, ct.getDonGia());
            lenh.setBoolean(5, ct.isLaQuaTangKem());
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        String truyVan = "UPDATE ChiTietHoaDon SET soLuong = ?, donGia = ?, laQuaTangKem = ? WHERE maHoaDon = ? AND maDonVi = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setInt(1, ct.getSoLuong());
            lenh.setDouble(2, ct.getDonGia());
            lenh.setBoolean(3, ct.isLaQuaTangKem());
            lenh.setString(4, ct.getHoaDon().getMaHoaDon());
            lenh.setString(5, ct.getDonViQuyDoi().getMaDonVi());
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD, String maDV) {
        int soDongThayDoi = 0;
        String truyVan = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maDonVi = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, maHD);
            lenh.setString(2, maDV);
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}