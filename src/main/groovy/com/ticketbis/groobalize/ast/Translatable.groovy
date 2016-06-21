package com.ticketbis.groobalize.ast

import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(["com.ticketbis.groobalize.ast.TranslatableTransformation"])
@interface Translatable {
    Class with();
}
