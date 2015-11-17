package test

import demo.Book
import demo.BookTranslation
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.springframework.context.i18n.LocaleContextHolder as LCH

class TranslationCriteriaIntegrationSpec extends IntegrationSpec {

    def book
    def inspiredBook

    def sessionFactory

    def setup() {
        book = new Book(author: "Dummy author", releaseDate: new Date())

        book.addToTranslations(title: "English title", synopsis: "An English synopsis", locale: new Locale('en'))
        book.addToTranslations(title: "American English title", synopsis: null, locale: new Locale('en', 'US'))
        book.addToTranslations(title: "American Pirate English title", synopsis: null, locale: new Locale('en', 'US', 'pirate'))
        book.addToTranslations(title: "Titulo en castellano", synopsis: "Sinopsis en castellano", locale: new Locale('es'))
        book.addToTranslations(title: "Titulo en argentino", synopsis: "Sinopsis en argentino", locale: new Locale('es', 'AR'))
        book.addToTranslations(title: "Titulo en español", synopsis: null, locale: new Locale('es', 'ES'))

        book.save(flush: true)

        inspiredBook = new Book(author: "Inspired author", releaseDate: new Date())
        inspiredBook.inspiredBy = book

        inspiredBook.addToTranslations(title: "Inspired book with English title", synopsis: null, locale: new Locale('en'))
        inspiredBook.addToTranslations(title: "Inspired book with Pirate English title", synopsis: null, locale: new Locale('en', 'US', 'pirate'))
        inspiredBook.addToTranslations(title: "Titulo en español", synopsis: null, locale: new Locale('es', 'ES'))

        inspiredBook.save(flush: true)
    }

    def cleanup() {
        inspiredBook.delete()
        book.delete()
    }

    void "criteria should only fetch requested locales"() {
    given:
        sessionFactory.cache.evictEntityRegion(Book.name)
        sessionFactory.cache.evictEntityRegion(BookTranslation.name)
        def book1 = Book.createCriteria().get {
            idEq(book.id)
            withTranslations([Locale.US])
        }

        sessionFactory.cache.evictEntityRegion(Book.name)
        sessionFactory.cache.evictEntityRegion(BookTranslation.name)
        def book2 = Book.createCriteria().get {
            idEq(book.id)
            withTranslations([Locale.FRENCH])
        }
    expect:
        Locale.US in book1.translations*.locale
        book2 != null
    }

    void "fetchTranslations criteria retrieves all the translations, but not the relations translations"() {
    given:
        def books
        Book.withNewSession {
            books = Book.createCriteria().list {
                idEq(book.id)
                // Fetch all translations except related and inspiredBy
                fetchTranslations()
            }
        }

    expect:
        books[0].translations.size() == 6
        new Locale('en') in books[0].translations*.locale
        new Locale('en', 'US') in books[0].translations*.locale
        new Locale('en', 'US', 'pirate') in books[0].translations*.locale
        new Locale('es') in books[0].translations*.locale
        new Locale('es', 'ES') in books[0].translations*.locale
        new Locale('es', 'AR') in books[0].translations*.locale

        !GrailsHibernateUtil.isInitialized(books[0], "related")
        !GrailsHibernateUtil.isInitialized(books[0].inspiredBy, "translations")
    }

    void "withTranslations criteria only retrieves the asked locale translation"() {
    given:
        def books
        Book.withNewSession {
            books = Book.createCriteria().list {
                idEq(book.id)
                // Fetch pirate translations for related
                withTranslations('related', [new Locale('en', 'US', 'pirate')])
            }
        }

    expect:
        1 == books[0].related[0].translations.size()
        new Locale('en', 'US', 'pirate') == books[0].related[0].translations[0].locale
    }

    void "withDefaultTranslations criteria only retrieves the asked locale and its fallbacks translations"() {
    given:
        LCH.locale = new Locale('en', 'US', 'pirate')

    when:
        def books
        Book.withNewSession {
            books = Book.createCriteria().list {
                idEq(inspiredBook.id)
                // Fetch current locale for inspiredBy
                withDefaultTranslations('inspiredBy')
            }
        }

    then:
        books[0].inspiredBy.translations.size() == 3
        new Locale('en') in books[0].inspiredBy.translations*.locale
        new Locale('en', 'US') in books[0].inspiredBy.translations*.locale
        new Locale('en', 'US', 'pirate') in books[0].inspiredBy.translations*.locale
    }

    void "namedQueries should work properly"() {
    given:
        def book1 = Book.includeTranslations().get(book.id)

        LCH.locale = new Locale('es', 'ES')
        def book2 = Book.translated().get(book.id)
    expect:
        book1.translations.size() > 0
        book1.getTranslation(Locale.US) != null

        book2.translations.size() > 0
        book1.getTranslation(new Locale('es', 'ES')) != null
    }

}
