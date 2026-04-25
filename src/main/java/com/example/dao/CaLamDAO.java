package com.example.dao;

import com.example.entity.CaLam;

public class CaLamDAO extends GenericDAO<CaLam, String> {

    @Override
    protected Class<CaLam> getEntityClass() {
        return CaLam.class;
    }
}
