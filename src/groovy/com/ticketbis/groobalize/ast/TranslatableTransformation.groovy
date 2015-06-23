package com.ticketbis.groobalize.ast

import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.Parameter

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class TranslatableTransformation implements ASTTransformation {

    private final static NON_TRANSLATABLE_FIELDS = [
        'id', 'version', 'errors', 'lastUpdated', 'dateCreated'
    ]

    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        assert astNodes[0] instanceof AnnotationNode
        assert astNodes[1] instanceof ClassNode,
                "@Translatable can only be applied on classes"

        AnnotationNode annotation = astNodes[0]
        ClassNode translatableClassNode = astNodes[1]

        assert annotation.members['with'] instanceof ClassExpression,
                "Invalid translation class specified."

        ClassExpression translateWithClass = annotation.members['with']

        addHasManyTranslations(translatableClassNode, translateWithClass)

        addProxyGetters(translatableClassNode, translateWithClass)
    }

    private void addHasManyTranslations(ClassNode classNode, ClassExpression translationClass) {
        def hasManyField = classNode.getField('hasMany')
        def hasManyMap = hasManyField.initialExpression

        hasManyMap.addMapEntryExpression(
                new ConstantExpression('translations'),
                translationClass);
    }

    private void addProxyGetters(ClassNode classNode, ClassExpression translationClass) {
        def translatableFields = getTranslatableFields(translationClass.type)
        translatableFields.each { field ->
            String fieldName = field.name
            String getterName = GrailsClassUtils.getGetterName(fieldName)
            def getterCode = new AstBuilder().buildFromString("""
                translations.first()?.$fieldName
            """).last()

            def methodNode = new MethodNode(
                    getterName,
                    FieldNode.ACC_PUBLIC,
                    field.type,
                    Parameter.EMPTY_ARRAY,
                    ClassHelper.EMPTY_TYPE_ARRAY,
                    getterCode)

            classNode.addMethod(methodNode)
            makeFieldTransient(classNode, fieldName)
        }

    }

    private void makeFieldTransient(ClassNode classNode, String name) {
        def transients = getOrCreateTransientsField(classNode).initialExpression
        transients.addExpression(new ConstantExpression(name))
    }

    private FieldNode getOrCreateTransientsField(ClassNode classNode) {
        getOrCreateField(classNode, 'transients', new ListExpression())
    }

    private FieldNode getOrCreateField(ClassNode classNode, String name, Expression initialExpression) {
        if (!classNode.getDeclaredField(name)) {
            def field = new FieldNode(
                    name,
                    FieldNode.ACC_PUBLIC | FieldNode.ACC_STATIC,
                    new ClassNode(Object),
                    classNode,
                    initialExpression)

            field.setDeclaringClass(classNode)
            classNode.addField(field)
            return field
        }
        classNode.getDeclaredField(name)
    }

    private List<FieldNode> getTranslatableFields(ClassNode translationClass) {
        translationClass.fields.findAll {
            !(it.modifiers & FieldNode.ACC_STATIC) &&
            !(it.name in NON_TRANSLATABLE_FIELDS)
        }
    }

}
