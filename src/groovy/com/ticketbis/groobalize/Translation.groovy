package com.ticketbis.groobalize

import groovy.transform.TypeCheckingMode
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
abstract class Translation<T> {
    Locale locale
    T source

    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    static void translationConstraints(delegate) {
        delegate.locale(unique: ['source'])
    }

    static constraints = {
        Translation.translationConstraints(delegate)
    }
}
