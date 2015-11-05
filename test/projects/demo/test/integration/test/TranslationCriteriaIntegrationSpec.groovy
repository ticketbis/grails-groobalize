package test

import demo.*
import org.springframework.context.i18n.LocaleContextHolder as LCH
import com.ticketbis.groobalize.WithFallbackLocaleContext
import grails.test.spock.IntegrationSpec
import org.springframework.web.context.request.RequestContextHolder as RCH

class TranslationCriteriaIntegrationSpec extends IntegrationSpec {

    def book

    def sessionFactory

    def setup() {
        book = new Book(author: "Dummy author", releaseDate: new Date())

        book.addToTranslations(title: "english title", synopsis: "an english synopsis", locale: new Locale('en'))
        book.addToTranslations(title: "american english title", synopsis: null, locale: new Locale('en', 'US'))
        book.addToTranslations(title: "american pirate english title", synopsis: null, locale: new Locale('en', 'US', 'pirate'))
        book.addToTranslations(title: "titulo en castellano", synopsis: "sinopsis en castellano", locale: new Locale('es'))
        book.addToTranslations(title: "titulo en argentino", synopsis: "sinopsis en argentino", locale: new Locale('es', 'AR'))
        book.addToTranslations(title: "titulo en español", synopsis: null, locale: new Locale('es', 'ES'))

        book.save(flush: true)
    }

    def cleanup() {
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

    void "namedQueries should be work properly"() {
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
