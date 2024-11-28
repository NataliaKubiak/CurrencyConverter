package repository;

import java.util.List;

public interface Crudable<T> {

    List<T> findAll();

    T create(T entity);
}
