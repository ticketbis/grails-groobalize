# Groobalize

Internacionalization plugin for grails inspired by [Gloobalize](https://github.com/globalize/globalize)

## Installation

Add dependency to your BuildConfig;

```groovy
compile "com.ticketbis.groobalize:groobalize:0.0.1"
```

## Usage

Simple usage example for a book with translateable title:

```groovy
// grails-app/domain
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
```

```groovy
// grails-app/domain
import com.ticketbis.groobalize.Translation

class BookTranslation extends Translation<Book> {
    String title
}
```
