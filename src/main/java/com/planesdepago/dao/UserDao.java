/**
 * 
 */
package com.planesdepago.dao;

import javax.persistence.EntityManager;

import com.planesdepago.entities.User;

/**
 * @author planesdepago
 *
 */
public class UserDao extends AbstractFacade<User> {

	public UserDao(EntityManager entityManager) {
		super(User.class, entityManager);
	}

}
