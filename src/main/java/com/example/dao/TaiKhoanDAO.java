package com.example.dao;

import com.example.entity.TaiKhoan;

public class TaiKhoanDAO extends GenericDAO<TaiKhoan, String> {

    @Override
    protected Class<TaiKhoan> getEntityClass() {
        return TaiKhoan.class;
    }
}
