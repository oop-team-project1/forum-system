package com.company.web.forum.helpers;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FilterOptionsPosts {
    public FilterOptionsPosts() {
        this(null,null,null,null,null,null,null,null,null,null);
    }

    private Optional<String> orderBy;
    private Optional<String> order;
    private Optional<String> author;
    @Size(max = 50)
    private Optional<String> title;
    @Size(max = 100)
    private Optional<String> content;
    @Size(max = 30)
    private Optional<String> keyword;
    private Optional<LocalDate> dateFrom;
    private Optional<LocalDate> dateUntil;
    private Optional<List<String>> tags;
    private Optional<List<String>> tagsExclude;

    public FilterOptionsPosts(
            String author,
            String title,
            String content,
            String keyword,
            LocalDate dateFrom,
            LocalDate dateUntil,
            List<String> tags,
            List<String> tags_exclude,
            String orderBy,
            String order
    ) {
        this.author = Optional.ofNullable(author);
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.keyword = Optional.ofNullable(keyword);
        this.dateFrom = Optional.ofNullable(dateFrom);
        this.dateUntil = Optional.ofNullable(dateUntil);
        this.tags = Optional.ofNullable(tags);
        this.tagsExclude = Optional.ofNullable(tags_exclude);
        this.order = Optional.ofNullable(order);
        this.orderBy = Optional.ofNullable(orderBy);
    }

    public Optional<String> getAuthor() {
        return author;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getContent() {
        return content;
    }

    public Optional<String> getKeyword() {
        return keyword;
    }

    public Optional<LocalDate> getDateFrom() {
        return dateFrom;
    }

    public Optional<LocalDate> getDateUntil() {
        return dateUntil;
    }

    public Optional<List<String>> getTags() {
        return tags;
    }

    public Optional<List<String>> getTags_exclude() {
        return tagsExclude;
    }

    public Optional<String> getOrderBy() {
        return orderBy;
    }

    public Optional<String> getOrder() {
        return order;
    }


}
