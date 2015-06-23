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

```groovy
def book = new Book(author: "Endika", releaseDate: new Date())

book.addToTranslations(title: "english title", locale: new Locale('en'))
book.addToTranslations(title: "american english title", locale: new Locale('en', 'US'))
book.addToTranslations(title: "british english title", locale: new Locale('en', 'GB'))

book.title // => american english title

import org.springframework.context.i18n.LocaleContextHolder as LCH
LCH.locale = new Locale('en', 'AU')

book.title // => english title
```

Eagerly fetch translations:

```groovy
def books = Book.includeTranslations([new Locale('en', 'US'), new Locale('en')]).list()

books*.translations*.locale // => [[en, en_US], [en, en_US], [en, en_US], [en, en_US], [en_US, en]]
```
