package com.example.dao;

import com.example.entity.SanPham;

public class SanPhamDAO extends GenericDAO<SanPham, String> {

    @Override
    protected Class<SanPham> getEntityClass() {
        return SanPham.class;
    }
}
