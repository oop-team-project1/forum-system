package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptions;
import com.company.web.forum.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository
{
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public List<User> getAll(FilterOptions filterOptions)
    {
        try (Session session = sessionFactory.openSession())
        {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public User getById(int id)
    {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null)
            {
                //if the exception is the same as in beerTag project =>
                //throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public User getByUsername(String username)
    {
        try (Session session = sessionFactory.openSession())
        {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> result = query.list();
            if (result.isEmpty())
            {
                //if the exception is the same as in beerTag project =>
                //throw new EntityNotFoundException("User", "username", username);
            }

            return result.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession())
        {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);

            List<User> result = query.list();
            if (result.isEmpty())
            {
                //if the exception is the same as in beerTag project =>
                //throw new EntityNotFoundException("User", "username", username);
            }

            return result.get(0);
        }
    }

    @Override
    public void create(User user)
    {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user)
    {
        //we should make the username column: updatable = false, because

//we dont want to update it
        try (Session session = sessionFactory.openSession())
        {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }
}