package com.ticketbis.groobalize

import groovy.transform.CompileStatic

@CompileStatic
class LocaleContext implements org.springframework.context.i18n.LocaleContext {

    final List<Locale> preferredLocales

    LocaleContext(Locale locale) {
        preferredLocales = [locale]
    }

    LocaleContext(List<Locale> locales) {
        preferredLocales = locales
    }

    Locale getLocale() {
        preferredLocales?.first()
    }
}

