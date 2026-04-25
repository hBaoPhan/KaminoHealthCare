package com.example.dao;

import com.example.entity.DonViQuyDoi;

public class DonViQuyDoiDAO extends GenericDAO<DonViQuyDoi, String> {

    @Override
    protected Class<DonViQuyDoi> getEntityClass() {
        return DonViQuyDoi.class;
    }
}
