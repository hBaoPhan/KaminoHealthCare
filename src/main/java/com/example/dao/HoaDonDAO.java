package com.example.dao;

import com.example.entity.HoaDon;

public class HoaDonDAO extends GenericDAO<HoaDon, String> {

    @Override
    protected Class<HoaDon> getEntityClass() {
        return HoaDon.class;
    }
}
