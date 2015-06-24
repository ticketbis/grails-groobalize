package com.ticketbis.groobalize.ast

import groovy.transform.PackageScope
import groovy.transform.CompileStatic

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.builder.AstBuilder

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils

@PackageScope
@CompileStatic
class GroobalizeASTUtils {

    static FieldNode getOrCreateField(
            ClassNode classNode,
            String fieldName,
            Expression initialExpression,
            int modifiers = FieldNode.ACC_PUBLIC,
            Class fieldType = Object) {

        if (!classNode.getDeclaredField(fieldName)) {
            FieldNode field = new FieldNode(
                    fieldName,
                    modifiers,
                    new ClassNode(Object),
                    classNode,
                    initialExpression)

            field.declaringClass = classNode
            classNode.addField(field)
            return field
        }
        classNode.getDeclaredField(fieldName)
    }

    static FieldNode getOrCreateStaticField(
            ClassNode classNode,
            String fieldName,
            Expression initialExpression,
            int modifiers = FieldNode.ACC_PUBLIC,
            Class fieldType = Object) {

        getOrCreateField(
                classNode,
                fieldName,
                initialExpression,
                modifiers | FieldNode.ACC_STATIC,
                fieldType)
    }

    static ListExpression getTransientsListExpression(ClassNode classNode) {
        FieldNode transientsField = getOrCreateStaticField(
                classNode,
                GrailsDomainClassProperty.TRANSIENT,
                new ListExpression())

        (ListExpression) transientsField.initialExpression
    }

    static MapExpression getHasManyMapExpression(ClassNode classNode) {
        FieldNode transientsField = getOrCreateStaticField(
                classNode,
                GrailsDomainClassProperty.HAS_MANY,
                new MapExpression())

        (MapExpression) transientsField.initialExpression
    }

    static ClosureExpression getNamedQueriesClosureExpression(ClassNode classNode) {
        FieldNode namedQueriesField = getOrCreateStaticField(
                classNode,
                GrailsDomainClassProperty.NAMED_QUERIES,
                new ClosureExpression(GrailsASTUtils.ZERO_PARAMETERS,
                                      new BlockStatement()))

        (ClosureExpression) namedQueriesField.initialExpression
    }

    static void addTransient(ClassNode classNode, String fieldName) {
        ListExpression transientsListExpr = getTransientsListExpression(classNode)
        transientsListExpr.addExpression(new ConstantExpression(fieldName))
    }

    static void addHasManyRelationship(
            ClassNode classNode,
            String relName,
            ClassNode relClass) {

        MapExpression hasManyMapExpr = getHasManyMapExpression(classNode)
        hasManyMapExpr.addMapEntryExpression(
                new ConstantExpression(relName),
                new ClassExpression(relClass))
    }

    static void addNamedQuery(ClassNode classNode, Statement code) {
        ClosureExpression namedQueriesExpr = getNamedQueriesClosureExpression(classNode)
        BlockStatement blockStmnt = (BlockStatement) namedQueriesExpr.code
        blockStmnt.addStatement(code)
    }

}
