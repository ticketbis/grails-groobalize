package test

import demo.*
import org.springframework.context.i18n.LocaleContextHolder as LCH
import com.ticketbis.groobalize.WithFallbackLocaleContext
import grails.test.spock.IntegrationSpec

class TranslationResolutionIntegrationSpec extends IntegrationSpec {

    def book

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

    void "test simple locales fallback"() {
    given:
        LCH.locale = new Locale('es', 'ES')
    expect:
        book.title == "titulo en español"
        book.synopsis == "sinopsis en castellano"
    }

    void "test locales fallback"() {
    given:
        LCH.locale = new Locale('en', 'US', 'pirate')
    expect:
        book.title == "american pirate english title"
        book.synopsis == "an english synopsis"
    }

    void "test custom fallbacks"() {
    given:
        LCH.localeContext = new WithFallbackLocaleContext([
            new Locale('es', 'UY'),
            new Locale('es', 'ES'),
            new Locale('en', 'US'),
            new Locale('en')])
    expect:
        book.title == null // Name has no inheritance
        book.synopsis == "an english synopsis"
    }

}
