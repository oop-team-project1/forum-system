package com.company.web.forum.repositories;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getAll(FilterOptionsPosts filterOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filterOptions.getAuthor().ifPresent(value -> {
                filters.add("createdBy.username like :author");
                params.put("author", String.format("%%%s%%", value));
            });

            if(filterOptions.getKeyword().isEmpty()) {

                filterOptions.getContent().ifPresent(value -> {
                    filters.add("content like :content");
                    params.put("content", String.format("%%%s%%", value));
                });
                filterOptions.getTitle().ifPresent(value -> {
                    filters.add("title like :title");
                    params.put("title", String.format("%%%s%%", value));
                });
            }

            filterOptions.getKeyword().ifPresent(value -> {
                filters.add("(title like :keyword or content like :keyword)");
                params.put("keyword", String.format("%%%s%%", value));
            });

            filterOptions.getDateFrom().ifPresent(value -> {
                filters.add("date_of_creation >= :dateFrom");
                params.put("dateFrom",value);
            });
            filterOptions.getDateUntil().ifPresent(value -> {
                filters.add("date_of_creation <= :dateUntil");
                params.put("dateUntil", value);
            });
            filterOptions.getTags().ifPresent(value -> {
                filters.add("JOIN tags AS t WHERE t.name  IN (:tags) ");
                params.put("tags", value);
            });
            filterOptions.getTags_exclude().ifPresent(value -> {
                filters.add("JOIN tags AS te WHERE te.name NOT IN (:tags_exclude)");
                params.put("tags", value);
            });

            StringBuilder queryString = new StringBuilder("from Post");
            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }
            queryString.append(generateOrderBy(filterOptions));

            Query<Post> query = session.createQuery(queryString.toString(),Post.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Post get(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }


    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }


    @Override
    public void delete(int id) {
        Post postToDelete = get(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(postToDelete);
            session.getTransaction().commit();
        }

    }

    @Override
    @Modifying
    @Transactional
    public void deleteMultiple(List<Integer> ids, User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "DELETE FROM Post WHERE id IN (:ids) AND NOT EXISTS (" +
                    "SELECT 1 FROM Post WHERE id IN (:ids) AND createdBy != :user )";
            Query<Post> query = session.createQuery(hql);
            query.setParameterList("ids", ids);
            query.setParameter("user",user);
            query.executeUpdate();
            session.getTransaction().commit();
        }

    }

    @Override
    public List<Integer> filterNonUserPostIds(List<Integer> ids, User user) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT id FROM Post WHERE id IN (:ids) AND createdBy != :user";

            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameterList("ids",ids);
            query.setParameter("user",user);

            return query.list();
        }

    }

    private String generateOrderBy(FilterOptionsPosts filterOptions) {
        if (filterOptions.getOrder().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (filterOptions.getOrder().get()) {
            case "likes":
                orderBy = "likes";
                break;
            case "date":
                orderBy = "date_posted";
                break;
            case "comments":
                orderBy = "comments";

        }

        orderBy = String.format(" order by %s", orderBy);

        if (filterOptions.getOrderBy().isPresent() && filterOptions.getOrderBy().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
