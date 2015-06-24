package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.springframework.context.i18n.LocaleContext

@CompileStatic
class GroobalizeHelper {
    static Translation getPreferredTranslation(
            Collection<Translation> translations,
            LocaleContext context = LCH.getLocaleContext()) {

        List<Locale> preferredLocales = retrivePreferredLocales(context)

        preferredLocales.findResult { locale ->
            translations.find { it.locale == locale }
        }
    }

    static List<Locale> retrivePreferredLocales(LocaleContext context = LCH.getLocaleContext()) {
        if (context instanceof WithFallbackLocaleContext) {
            return ((WithFallbackLocaleContext) context).preferredLocales
        }
        getLocaleTree(context.locale)
    }

    static List<Locale> getLocaleTree(Locale locale) {
        if (!locale) return []

        List<String> elements = (List<String>) [locale.language,
                                  locale.country, locale.variant].findAll()

        List<Locale> locales = []
        switch(elements.size()) {
            case 3: locales << locale
            case 2: locales << new Locale(elements[0], elements[1])
            case 1: locales << new Locale(elements[0])
        }
        locales
    }
}
