package com.example.dao;

import com.example.entity.NhanVien;

public class NhanVienDAO extends GenericDAO<NhanVien, String> {

    @Override
    protected Class<NhanVien> getEntityClass() {
        return NhanVien.class;
    }
}
