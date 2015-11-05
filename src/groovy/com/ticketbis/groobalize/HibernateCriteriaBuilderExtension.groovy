package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import grails.orm.HibernateCriteriaBuilder as CriteriaBuilder
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions
import static org.hibernate.criterion.CriteriaSpecification.LEFT_JOIN

@CompileStatic
final class HibernateCriteriaBuilderExtension {
    static void fetchTranslations(
            CriteriaBuilder builder,
            String path = null,
            String alias = null,
            Collection<Locale> locales = null) {

        String translationsPath = [path, 'translations'].findAll().join('.')
        if (!alias)
            alias = [path?.replace('.', '_'), 't'].findAll().join('_')

        builder.resultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        if (path) {
            builder.createAlias(path, path.replace('.', '_'), LEFT_JOIN)
        }
        if (locales) {
            builder.instance.createAlias(translationsPath, alias, LEFT_JOIN,
                    Restrictions.in(alias + '.locale', locales))

            // We need to set locale filter also in where clause to avoid lazy
            // translations reload
            builder.or {
                builder.isNull(alias + '.id')
                builder.in(alias + '.locale', locales)
            }
        } else {
            builder.instance.createAlias(translationsPath, alias, LEFT_JOIN)
        }
    }

    static void withTranslations(
            CriteriaBuilder builder,
            String path,
            Collection<Locale> locales = null) {

        fetchTranslations(builder, path, null, locales)
    }

    static void withTranslations(
            CriteriaBuilder builder,
            Collection<Locale> locales = null) {

        fetchTranslations(builder, null, null, locales)
    }

    static void withDefaultTranslations(
            CriteriaBuilder builder,
            String path = null) {

        Collection<Locale> locales =
                GroobalizeHelper.retrivePreferredLocales()

        fetchTranslations(builder, path, null, locales)
    }
}
