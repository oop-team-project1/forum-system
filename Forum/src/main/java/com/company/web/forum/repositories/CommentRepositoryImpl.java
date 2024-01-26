package com.company.web.forum.repositories;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAll(FilterOptionsPosts filterOptionsPosts) {
        return null;
    }

    @Override
    public Comment getById(int id) {
        try(Session session = sessionFactory.openSession() ){
            Comment comment = session.get(Comment.class, id);
            if(comment == null){
                throw new EntityNotFoundException("Comment", id);
            }
            return comment;
        }
    }

    @Override
    public void create(Comment comment) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }
}
