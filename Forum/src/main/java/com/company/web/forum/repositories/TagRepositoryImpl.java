package com.company.web.forum.repositories;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private static final String GET_TAG_BY_NAME = "SELECT t " +
            "FROM Tag t " +
            "WHERE t.name = :name";
    private final SessionFactory sessionFactory;
    private final PostRepository postRepository;

    public TagRepositoryImpl(SessionFactory sessionFactory, PostRepository postRepository) {
        this.postRepository = postRepository;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Tag tag, int postId) {
        try (Session session = sessionFactory.openSession()) {
            Post post = postRepository.get(postId);

            Query<Tag> query = session.createQuery(GET_TAG_BY_NAME, Tag.class);
            query.setParameter("name", tag.getName());
            Tag tagExisting = query.getSingleResult();

            session.beginTransaction();
            if (tagExisting == null) {
                session.persist(tag);
                post.getTags().add(tag);
            } else {
                post.getTags().add(tagExisting);
            }
            session.getTransaction().commit();
        }
    }


    @Override
    public void delete(int tagId, int postId) {
        Tag tagToDelete = get(tagId);
        Post post = postRepository.get(postId);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            post.getTags().remove(tagToDelete);
            session.getTransaction().commit();
        }
    }

    @Override
    public Tag get(int tagId) {
        try (Session session = sessionFactory.openSession()) {
            Tag tag = session.get(Tag.class, tagId);
            if (tag == null) {
                throw new EntityNotFoundException("Tag", tagId);
            }
            return tag;
        }
    }

    @Override
    public List<Tag> getAll(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("select tags from Post where id = :postId", Tag.class);
            query.setParameter("postId", postId);
            return query.list();
//            String SQL = "Select * FROM posts_tags p WHERE post_id = ? ";
        }
    }

    @Override
    public List<Tag> getTrending(int amount) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("select t from Tag t order by size(t.posts) desc", Tag.class).setMaxResults(10);
            return query.list();
        }
    }


}