package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic
trait Translated {
    abstract static Class getTranslationClass()

    abstract static List<String> getTranslatedFields()

    abstract Set<Translation> getTranslations()

    // translationsMapCache is lazily computed
    @PackageScope transient Map<Locale, Translation> translationsMapCache

    Translation getTranslation(Locale locale) {
        // Use cached version if available
        if (translationsMapCache)
            return translationsMapCache[locale]

        getTranslations().find { Translation t -> t.locale == locale }
    }

    Map<Locale, Translation> getTranslationByLocale() {
        if (!translationsMapCache) {
            translationsMapCache = new HashMap(getTranslations().size())
            getTranslations().each { Translation t ->
                translationsMapCache[t.locale] = t
            }
        }
        translationsMapCache
    }
}
