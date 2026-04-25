package com.example.dao;

import com.example.entity.Lo;

public class LoDAO extends GenericDAO<Lo, String> {

    @Override
    protected Class<Lo> getEntityClass() {
        return Lo.class;
    }
}
