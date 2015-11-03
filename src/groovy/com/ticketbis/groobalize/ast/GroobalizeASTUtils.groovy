package com.ticketbis.groobalize.ast

import groovy.transform.PackageScope
import groovy.transform.CompileStatic

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*

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
            ClassNode fieldType = ClassHelper.DYNAMIC_TYPE) {

        if (!classNode.getDeclaredField(fieldName)) {
            PropertyNode property = new PropertyNode(
                    fieldName,
                    modifiers,
                    fieldType,
                    classNode,
                    initialExpression,
                    null, null)

            classNode.addProperty(property)
            return property.field
        }
        classNode.getDeclaredField(fieldName)
    }

    static FieldNode getOrCreateStaticField(
            ClassNode classNode,
            String fieldName,
            Expression initialExpression,
            int modifiers = FieldNode.ACC_PUBLIC,
            ClassNode fieldType = ClassHelper.DYNAMIC_TYPE) {

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
                buildEmptyClosure())

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

    static ClosureExpression buildEmptyClosure() {
        ClosureExpression expr = new ClosureExpression(Parameter.EMPTY_ARRAY,
                new BlockStatement())
        expr.variableScope = new VariableScope()
        return expr
    }

}
