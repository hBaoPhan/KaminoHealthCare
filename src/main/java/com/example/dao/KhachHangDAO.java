package com.example.dao;

import com.example.entity.KhachHang;

public class KhachHangDAO extends GenericDAO<KhachHang, String> {

    @Override
    protected Class<KhachHang> getEntityClass() {
        return KhachHang.class;
    }
}
