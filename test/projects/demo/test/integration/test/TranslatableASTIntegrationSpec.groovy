package test

import demo.*
import grails.test.spock.IntegrationSpec
import com.ticketbis.groobalize.Translated

class TranslatableASTIntegrationSpec extends IntegrationSpec {

    def setup() {
    }

    def cleanup() {
    }

    void "test ASTs"() {
    expect:
        Book.translationClass == BookTranslation
        'title' in Book.translatedFields
        'synopsis' in Book.translatedFields
    }

    void "test Translated trait"() {
    given:
        def locale = new Locale('en', 'US')
        def book = new Book(author: "Dummy author", releaseDate: new Date())
        def translation = new BookTranslation(title: "title", synopsis: "synopsis", locale: locale)
        book.addToTranslations(translation)
    expect:
        book instanceof Translated
        translation == book.getTranslation(locale)
        locale in book.getTranslationByLocale().keySet()
        translation in book.getTranslationByLocale().values()
        // now getTranslation uses cached Map
        translation == book.getTranslation(locale)
    }

}
