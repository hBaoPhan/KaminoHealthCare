package com.example.dao;

import com.example.connectDB.ConnectDB;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericDAO<T, ID> {

    protected abstract Class<T> getEntityClass();

    public List<T> layTatCa() {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from " + getEntityClass().getSimpleName();
            return session.createQuery(hql, getEntityClass()).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public T timTheoMa(ID id) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(getEntityClass(), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(T entity) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            rollback(transaction);
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(T entity) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            rollback(transaction);
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(ID id) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(getEntityClass(), id);
            if (entity != null) {
                session.remove(entity);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            rollback(transaction);
            e.printStackTrace();
            return false;
        }
    }

    /** Rollback an toàn, tránh NullPointerException. */
    protected void rollback(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
