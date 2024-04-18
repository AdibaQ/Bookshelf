package com.example.googlebooksapi;

public class Book {
    private String title;
    private String authors;
    private String imageUrl;

    private String bookId;

    public Book(String title, String authors, String imageUrl, String bookId) {
        this.title = title;
        this.authors = authors;
        this.imageUrl = imageUrl;
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getImageUrl() {
        return "https://books.google.com/books/content?id=" + getBookId() + "&printsec=frontcover&img=1&zoom=1&edge=curl&imgtk=AFLRE725EDjl-apJnj_hFVX8rHsaTip_OyufLDX8qUDetLx3J1kf4RZx45_bAxMEkVr8_p5LpI27DO_G_Tyr4g0fbY7PlsNDpVtVqqgh-rt_GjQjoWv0TmKGsckhZsjGfwfRbRPRVlpS&source=gbs_api";
    }
    public String getBookId() {
        return bookId;
    }
}
