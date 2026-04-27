package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO extends GenericDAO<HoaDon, String> {

    @Override
    protected Class<HoaDon> getEntityClass() {
        return HoaDon.class;
    }

    public List<HoaDon> timKiem(String maHoaDon, LocalDate ngayTao) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("from HoaDon h where 1=1");

            if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
                hql.append(" and h.maHoaDon like :maHoaDon");
            }
            if (ngayTao != null) {
                hql.append(" and h.thoiGianTao >= :startOfDay and h.thoiGianTao < :startOfNextDay");
            }

            Query<HoaDon> query = session.createQuery(hql.toString(), HoaDon.class);

            if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
                query.setParameter("maHoaDon", "%" + maHoaDon.trim() + "%");
            }
            if (ngayTao != null) {
                query.setParameter("startOfDay", ngayTao.atStartOfDay());
                query.setParameter("startOfNextDay", ngayTao.plusDays(1).atStartOfDay());
            }

            List<HoaDon> list = query.list();
            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
            for (HoaDon h : list) {
                h.setDsChiTiet(ctDAO.layTheoMaHoaDon(h.getMaHoaDon()));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<HoaDon> layTatCa() {
        List<HoaDon> list = super.layTatCa();
        ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
        for (HoaDon h : list) {
            h.setDsChiTiet(ctDAO.layTheoMaHoaDon(h.getMaHoaDon()));
        }
        return list;
    }

    @Override
    public HoaDon timTheoMa(String id) {
        HoaDon h = super.timTheoMa(id);
        if (h != null) {
            h.setDsChiTiet(new ChiTietHoaDonDAO().layTheoMaHoaDon(h.getMaHoaDon()));
        }
        return h;
    }

    /**
     * Hàm thực thi toàn bộ luồng đổi hàng trong 1 Transaction duy nhất
     */
    public boolean luuGiaoDichDoiHang(HoaDon hoaDonMoi, 
                                      List<SuPhanBoLo> dsTraLai, 
                                      List<ChiTietHoaDon> dsChiTietMoi, 
                                      List<SuPhanBoLo> dsPhanBoMoi) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 1. XỬ LÝ HÀNG TRẢ: Cộng lại số lượng tồn kho cho các Lô cũ
            if (dsTraLai != null) {
                for (SuPhanBoLo spTra : dsTraLai) {
                    Lo lo = session.get(Lo.class, spTra.getLo().getMaLo());
                    if (lo != null) {
                        // SỬA: Dùng đúng thuộc tính soLuongSanPham của class Lo
                        lo.setSoLuongSanPham(lo.getSoLuongSanPham() + spTra.getSoLuong());
                        session.merge(lo); 
                    }
                }
            }

            // 2. LƯU HÓA ĐƠN MỚI
            session.persist(hoaDonMoi);

            // 3. LƯU CHI TIẾT HÓA ĐƠN MỚI
            if (dsChiTietMoi != null) {
                for (ChiTietHoaDon ct : dsChiTietMoi) {
                    ct.setHoaDon(hoaDonMoi); 
                    session.persist(ct);
                }
            }

            // 4. XỬ LÝ HÀNG MỚI: Trừ tồn kho và lưu Sự Phân Bổ Lô
            if (dsPhanBoMoi != null) {
                for (SuPhanBoLo spMoi : dsPhanBoMoi) {
                    Lo lo = session.get(Lo.class, spMoi.getLo().getMaLo());
                    if (lo != null) {
                        // SỬA: Dùng đúng thuộc tính soLuongSanPham của class Lo
                        int soLuongConLai = lo.getSoLuongSanPham() - spMoi.getSoLuong();
                        if (soLuongConLai < 0) {
                            throw new RuntimeException("Lô " + lo.getMaLo() + " không đủ số lượng để đổi!");
                        }
                        lo.setSoLuongSanPham(soLuongConLai);
                        session.merge(lo);
                    }
                    session.persist(spMoi);
                }
            }

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}