package groobalize

import grails.test.spock.IntegrationSpec
import com.ticketbis.groobalize.test.*

import grails.test.mixin.Mock

@Mock(Book)
class TranslationIntegrationSpec extends IntegrationSpec {

    Book book

    def setup() {
    }

    def cleanup() {
    }

    void "test book with translations construction"() {
    when:
        book = new Book(author: "Endika", releaseDate: new Date())

    then:
        book.save(flush: true)

    when:
        book.addToTranslations(title: "english title", locale: new Locale('en'))
        book.addToTranslations(title: "american english title", locale: new Locale('en', 'US'))
        book.addToTranslations(title: "american pirate english title", locale: new Locale('en', 'US', 'pirate'))
        book.addToTranslations(title: "titulo en castellano", locale: new Locale('es'))
        book.addToTranslations(title: "titulo en argentino", locale: new Locale('es', 'AR'))
        book.addToTranslations(title: "titulo en español", locale: new Locale('es', 'ES'))

    then:
        book.save(flush: true)
        book.translations.size() == 6
    }
}
