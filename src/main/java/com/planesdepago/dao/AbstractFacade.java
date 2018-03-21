/**
 * 
 */
package com.planesdepago.dao;

/**
 * @author planesdepago
 *
 */
import java.util.List;

import javax.persistence.EntityManager;

public abstract class AbstractFacade<T> {

	private final Class<T> entityClass;
	private EntityManager entityManager;

	public AbstractFacade(Class<T> entityClass, EntityManager entityManager) {
		this.entityClass = entityClass;
		this.entityManager = entityManager;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	public void create(T entity) {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(entity);
		getEntityManager().getTransaction().commit();
	}

	public void edit(T entity) {
		getEntityManager().getTransaction().begin();
		getEntityManager().merge(entity);
		getEntityManager().getTransaction().commit();
	}

	public void remove(T entity) {
		getEntityManager().getTransaction().begin();
		T find = getEntityManager().find(entityClass, entity);
		getEntityManager().remove(find);
		getEntityManager().getTransaction().commit();
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> findAll() {
		javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder()
				.createQuery(entityClass);
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	public void close() {
		entityManager.close();
	}

}
