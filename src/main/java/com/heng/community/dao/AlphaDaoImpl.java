package com.heng.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class AlphaDaoImpl  implements AlphaDao{
    @Override
    public String select() {
        return "select";
    }
}
