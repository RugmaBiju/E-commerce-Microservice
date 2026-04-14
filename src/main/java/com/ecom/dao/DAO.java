package main.java.com.ecom.dao;

import java.util.List;

// Interface = Abstraction + Polymorphism
public interface DAO<T> {
    void    create(T t)      throws Exception;
    T       findById(int id) throws Exception;
    List<T> findAll()        throws Exception;
    void    update(T t)      throws Exception;
    void    delete(int id)   throws Exception;
}
