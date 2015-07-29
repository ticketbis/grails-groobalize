package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import org.springframework.context.i18n.LocaleContext

@CompileStatic
class WithFallbackLocaleContext implements LocaleContext {

    final List<Locale> preferredLocales

    WithFallbackLocaleContext(Locale locale) {
        preferredLocales = [locale]
    }

    WithFallbackLocaleContext(List<Locale> locales) {
        preferredLocales = locales
    }

    Locale getLocale() {
        preferredLocales?.first()
    }
}

