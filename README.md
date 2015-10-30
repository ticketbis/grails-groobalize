# Groobalize

[![Build
Status](https://travis-ci.org/ticketbis/grails-groobalize.png?branch=master)](https://travis-ci.org/ticketbis/grails-groobalize)

Internacionalization plugin for grails inspired by [Gloobalize](https://github.com/globalize/globalize)

## Installation

Add dependency to your BuildConfig;

```groovy
compile "com.ticketbis:groobalize:0.2.1"
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

    static hasMany = [
        related: Book
    ]

    static mapping = { }
    static constraints = { }
    static namedQueries = { }
}
```

```groovy
// grails-app/domain
import com.ticketbis.groobalize.Translation

class BookTranslation extends Translation<Book> {
    @Field(inherit=false)
    String title

    String synopsis = null // Inherit from parent locale

    static constraints = {
        synopsis(nullable: true)
    }
}
```

```groovy
def book = new Book(author: "Endika", releaseDate: new Date())

book.addToTranslations(title: "english title", synopsis: "synopsis in english", locale: new Locale('en'))
book.addToTranslations(title: "american english title", locale: new Locale('en', 'US'))
book.addToTranslations(title: "british english title", locale: new Locale('en', 'GB'))

book.title // => american english title
book.synopsis // => synopsis in english

import org.springframework.context.i18n.LocaleContextHolder as LCH
LCH.locale = new Locale('en', 'AU')

book.title // => null // Title set as no-inheritable field
book.synopsis // => synopsis in english
```

#### Customizing fallbacks

Groobalize also includes a `WithFallbackLocaleContext` that
supports custom fallback.

```groovy
import com.ticketbis.groobalize.WithFallbackLocaleContext

LCH.localeContext = new WithFallbackLocaleContext([new Locale('en', 'AU'),
        new Locale('en', 'GB'), new Locale('en')])

book.title // => null
book.synopsis // => synopsis in english
```

#### Eagerly fetch translations

```groovy
def books = Book.includeTranslations([new Locale('en', 'US'), new Locale('en')]).list()

books*.translations*.locale // => [[en, en_US], [en, en_US], [en_US, en]]
```

This will fetch books with given translations in 1 query.

It's also supported on Criterias:

```groovy
// Fetch book with current translation
def books = Book.translated().list()

// Fetch book with all translation
def books = Book.includeTranslation().list()

books = Book.createCriteria().list {
    // Fetch all translations
    fetchTranslations()
    // Fetch current locale for inspiredBy
    withDefaultTranslations('inspiredBy')
    // Fetch english translations for related
    withTranslations('related', [new Locale('en')])
}
// No more queries from here 🎉
books.each {
    println([it.title, it.translations*.locale, it.inspiredBy?.translations*.title].join("\t"))
}
```

#### Customizing behaviour of translatable fields

```groovy
import com.ticketbis.groobalize.Translation
import com.ticketbis.groobalize.ast.Field

class BookTranslation extends Translation<Book> {
    String title

    @Field(inherit=false)
    String synopsis
}
```

Supported options are:

* `inherit`: when this option is set to false, proxy getter only look
  for translations that match exactly with first option of LocaleContext
* `skipGetter`: when this option is set to true, the proxy getter is not
  added to domain class
