package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import grails.orm.HibernateCriteriaBuilder as CriteriaBuilder
import org.hibernate.Criteria
import org.hibernate.criterion.CriteriaSpecification
import com.ticketbis.groobalize.GroobalizeHelper

@CompileStatic
class HibernateCriteriaBuilderExtension {
    static void fetchTranslations(
            CriteriaBuilder builder,
            String path = null,
            String alias = null) {

        String translationsPath = [path, 'translations'].findAll().join('.')
        if (!alias)
            alias = [path?.replace('.', '_'), 't'].findAll().join('_')

        builder.resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        if (path) {
            builder.createAlias(path, path.replace('.', '_'), CriteriaSpecification.LEFT_JOIN)
        }
        builder.createAlias(translationsPath, alias, CriteriaSpecification.LEFT_JOIN)
    }

    static void withTranslations(
            CriteriaBuilder builder,
            String path,
            Collection<Locale> locales = null) {

        String translationsPath = [path, 'translations'].findAll().join('.')
        String alias = [path?.replace('.', '_'), 't'].findAll().join('_')
        fetchTranslations(builder, path, alias)

        if (locales) {
            builder.or {
                builder.isEmpty(translationsPath)
                builder.'in'("${ alias }.locale", locales)
            }
        }
    }

    static void withTranslations(
            CriteriaBuilder builder,
            Collection<Locale> locales = null) {

        withTranslations(builder, null, locales)
    }

    static void withDefaultTranslations(
            CriteriaBuilder builder,
            String path = null) {

        Collection<Locale> locales =
                GroobalizeHelper.retrivePreferredLocales()

        withTranslations(builder, path, locales)
    }
}
