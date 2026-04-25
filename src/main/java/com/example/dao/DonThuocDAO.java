package com.example.dao;

import com.example.entity.DonThuoc;

public class DonThuocDAO extends GenericDAO<DonThuoc, String> {

    @Override
    protected Class<DonThuoc> getEntityClass() {
        return DonThuoc.class;
    }
}
