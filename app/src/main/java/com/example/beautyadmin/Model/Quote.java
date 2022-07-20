package com.example.beautyadmin.Model;

public class Quote {

    private String quote, quoteId;

    public Quote(String quote, String quoteId) {
        this.quote = quote;
        this.quoteId = quoteId;
    }

    public Quote() {
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }
}
