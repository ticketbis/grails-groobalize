package com.ticketbis.groobalize.ast

import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
@interface Field {
    boolean inherit()     default true;
    boolean skipGetter()  default false;
}
