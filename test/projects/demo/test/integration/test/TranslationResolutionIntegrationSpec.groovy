package test

import demo.*
import org.springframework.context.i18n.LocaleContextHolder as LCH
import grails.test.spock.IntegrationSpec

class TranslationResolutionIntegrationSpec extends IntegrationSpec {

    def book

    def setup() {
        book = new Book(author: "Dummy author", releaseDate: new Date())

        book.addToTranslations(title: "english title", synopsis: "an english synopsis", locale: new Locale('en'))
        book.addToTranslations(title: "american english title", synopsis: "an american english synopsis", locale: new Locale('en', 'US'))
        book.addToTranslations(title: "american pirate english title", synopsis: null, locale: new Locale('en', 'US', 'pirate'))
        book.addToTranslations(title: "titulo en castellano", synopsis: "sinopsis en castellano", locale: new Locale('es'))
        book.addToTranslations(title: "titulo en argentino", synopsis: "sinopsis en argentino", locale: new Locale('es', 'AR'))
        book.addToTranslations(title: "titulo en español", synopsis: null, locale: new Locale('es', 'ES'))

        book.save(flush: true)
    }

    def cleanup() {
        book.delete()
    }

    void "test basic locales fallback"() {
    given:
        LCH.locale = new Locale('es', 'ES')
    expect:
        book.title == "titulo en español"
        book.synopsis == "sinopsis en castellano"
    }
}
