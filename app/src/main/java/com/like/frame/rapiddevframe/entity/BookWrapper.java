package com.like.frame.rapiddevframe.entity;

import java.util.List;

/**
 * Created By Like on 2018/4/23.
 */

public class BookWrapper {

    private int count;
    private int start;
    private int total;
    private List<Book> books;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
