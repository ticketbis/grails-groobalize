package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.springframework.context.i18n.LocaleContext

@CompileStatic
class GroobalizeHelper {

    // ToDo Locale.ROOT should be soported
    static final Locale ROOT_LOCALE = null

    static def getField(Collection<Translation> translations,
            String property,
            boolean inherit = true,
            LocaleContext context = LCH.getLocaleContext()) {

        if (!translations || !context)
            return null

        List<Locale> preferredLocales = inherit ?
                retrivePreferredLocales(context) :
                (List<Locale>) [context?.locale].findAll()

        preferredLocales.findResult { locale ->
            translations.find { it.locale == locale }?.getProperty(property)
        }
    }

    static List<Locale> retrivePreferredLocales(LocaleContext context = LCH.getLocaleContext()) {
        if (context instanceof WithFallbackLocaleContext) {
            return ((WithFallbackLocaleContext) context).preferredLocales
        }
        getLocaleTree(context?.locale)
    }

    private static List<Locale> getLocaleTree(Locale locale) {
        if (!locale) return [ROOT_LOCALE]

        List<String> elements = (List<String>) [locale.language,
                                  locale.country, locale.variant].findAll()

        List<Locale> locales = []
        switch(elements.size()) {
            case 3: locales << locale
            case 2: locales << new Locale(elements[0], elements[1])
            case 1: locales << new Locale(elements[0])
            case 0: locales << ROOT_LOCALE
        }
        locales
    }
}
