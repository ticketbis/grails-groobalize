package com.ticketbis.groobalize

import groovy.transform.CompileStatic

@CompileStatic
abstract class Translation<T> {
    Locale locale
    T source
}
