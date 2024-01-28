package com.company.web.forum.repositories;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAll(FilterOptionsComments filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getContent().ifPresent(value -> {
                filters.add("content like :commentContent");
                params.put("commentContent", String.format("%%%s%%", value));
            });

            filterOptions.getUserId().ifPresent(value -> {
                filters.add("createdBy.id = :userId");
                params.put("userId", value);
            });

            filterOptions.getPostId().ifPresent(value -> {
                filters.add("post.id = :postId");
                params.put("postId", value);
            });

//            filterOptions.getStartDate().ifPresent(value -> {
//                filters.add("date >= :startDate");
//                params.put("startDate", value);
//            });
//
//            filterOptions.getEndDate().ifPresent(value -> {
//                filters.add("date >= :endDate");
//                params.put("endDate", value);
//            });
            StringBuilder queryString = new StringBuilder("from Comment");

            if (!filters.isEmpty()) {
                queryString.append(" where ")
                        .append(String.join(" and ", filters));
            }

            Query<Comment> query = session.createQuery(queryString.toString(), Comment.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Comment getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                throw new EntityNotFoundException("Comment", id);
            }
            return comment;
        }
    }

    @Override
    public void create(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }
}
