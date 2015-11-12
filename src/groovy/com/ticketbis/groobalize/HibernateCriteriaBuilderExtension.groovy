package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import grails.orm.HibernateCriteriaBuilder
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions
import static org.hibernate.criterion.CriteriaSpecification.LEFT_JOIN

@CompileStatic
final class HibernateCriteriaBuilderExtension {
    static void fetchTranslations(
            HibernateCriteriaBuilder builder,
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

        builder.instance.createAlias(translationsPath, alias, LEFT_JOIN)

        if (locales) {
            builder.instance.add(
                Restrictions.or(
                    Restrictions.in(alias + '.locale', locales),
                    Restrictions.isNull(alias + '.id')
                )
            )
        }
    }

    static void withTranslations(
            HibernateCriteriaBuilder builder,
            String path,
            Collection<Locale> locales = null) {

        fetchTranslations(builder, path, null, locales)
    }

    static void withTranslations(
            HibernateCriteriaBuilder builder,
            Collection<Locale> locales = null) {

        fetchTranslations(builder, null, null, locales)
    }

    static void withDefaultTranslations(
            HibernateCriteriaBuilder builder,
            String path = null) {

        Collection<Locale> locales =
                GroobalizeHelper.retrivePreferredLocales()

        fetchTranslations(builder, path, null, locales)
    }
}
