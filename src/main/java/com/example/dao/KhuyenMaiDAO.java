package com.example.dao;

import com.example.entity.KhuyenMai;
import com.example.entity.QuaTang;

import java.util.List;

/**
 * DAO cho KhuyenMai.
 * Thêm method nạp quà tặng kèm sau khi load.
 */
public class KhuyenMaiDAO extends GenericDAO<KhuyenMai, String> {

    private final QuaTangDAO quaTangDAO = new QuaTangDAO();

    @Override
    protected Class<KhuyenMai> getEntityClass() {
        return KhuyenMai.class;
    }

    /**
     * Lấy tất cả khuyến mãi và nạp quà tặng kèm vào từng đối tượng.
     */
    public List<KhuyenMai> layTatCaVoiQuaTang() {
        List<KhuyenMai> list = layTatCa();
        for (KhuyenMai km : list) {
            List<QuaTang> dsQT = quaTangDAO.timTheoKhuyenMai(km.getMaKhuyenMai());
            if (!dsQT.isEmpty()) {
                km.setQuaTangKem(dsQT.get(0));
            }
        }
        return list;
    }
}
