package com.ticketbis.groobalize

import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.util.logging.Log4j
import grails.orm.HibernateCriteriaBuilder
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions
import org.hibernate.impl.CriteriaImpl
import org.hibernate.impl.CriteriaImpl.Subcriteria
import static org.hibernate.criterion.CriteriaSpecification.LEFT_JOIN

@Log4j
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

        log.trace "Fetching translations at $translationsPath ($locales)"

        CriteriaImpl criteria = builder.instance as CriteriaImpl
        if (locales) {
            criteria.createAlias(translationsPath, alias, LEFT_JOIN,
                    Restrictions.in(alias + '.locale', locales))

            patchTransationsSubcriteria(criteria, translationsPath)
        } else {
            criteria.createAlias(translationsPath, alias, LEFT_JOIN)
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

    /*
     * This is a workaround to avoid the HHH-2049
     * (https://hibernate.atlassian.net/browse/HHH-2049).
     * When relation is fetched with alias and that alias has restrictions,
     * child collection is always lazily fetched. We need to fetch relation
     * eagerly and filtered.
     */
    @CompileDynamic
    private static void patchTransationsSubcriteria(
            CriteriaImpl criteria,
            String path) {

        try {
            Subcriteria subcriteria = criteria.iterateSubcriteria()
                    .find { Subcriteria sc -> sc.path == path }

            // Private field access
            subcriteria.@hasRestriction = false
        } catch (Exception ex) {
            log.error "Error patching translations subcriteria, " +
                + "translations will be lazily fetched: $ex"
        }
    }

}
