/**
 * 
 */
package com.planesdepago.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

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
		getEntityManager().remove(getEntityManager().merge(entity));
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

	//Existe en la base de datos ?
public boolean exists(Object key) {
		EntityManager em = getEntityManager();
		Metamodel metamodel = em.getMetamodel();
		EntityType<T> entity = metamodel.entity(entityClass);
		SingularAttribute<T, ? extends Object> declaredId = entity.getDeclaredId(key.getClass());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		javax.persistence.criteria.CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> from = cq.from(entityClass);
		Predicate condition = cb.equal(from.get(declaredId), key);
		cq.where(condition);
		TypedQuery<T> q = em.createQuery(cq);
		return q.getResultList().isEmpty();
	}

	public void close() {
		entityManager.close();
	}

}
