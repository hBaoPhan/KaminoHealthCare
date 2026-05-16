package com.example.service;

import com.example.dao.DonThuocDAO;
import com.example.entity.DonThuoc;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Đơn thuốc.
 */
public class DonThuocService {

    private final DonThuocDAO donThuocDAO = new DonThuocDAO();

    // ==================== TRUY VẤN ====================

    public List<DonThuoc> layTatCa() {
        return donThuocDAO.layTatCa();
    }

    public DonThuoc timTheoMa(String maDT) {
        return donThuocDAO.timTheoMa(maDT);
    }

    // ==================== CRUD ====================

    public boolean them(DonThuoc dt) {
        return donThuocDAO.them(dt);
    }

    public boolean capNhat(DonThuoc dt) {
        return donThuocDAO.capNhat(dt);
    }

    public boolean xoa(String maDT) {
        return donThuocDAO.xoa(maDT);
    }
}
