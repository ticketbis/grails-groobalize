package test

import demo.*
import grails.test.spock.IntegrationSpec

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
}
