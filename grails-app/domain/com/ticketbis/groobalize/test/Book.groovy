package com.ticketbis.groobalize.test

import com.ticketbis.groobalize.ast.Translatable

@Translatable(with = BookTranslation)
class Book {

    String author
    Date releaseDate
    Book inspiredBy

    static constraints = {
    }

    static hasMany = [
        related: Book
    ]

    static mapping = {
    }
}
