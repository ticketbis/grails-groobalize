package com.ticketbis.groobalize.ast

import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils
import org.codehaus.groovy.ast.builder.AstBuilder

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*

import com.ticketbis.groobalize.Translation

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

        ClassExpression translateWithExpr = annotation.members['with']
        ClassNode translateWithClass = translateWithExpr.type

        assert GrailsASTUtils.isDomainClass(translatableClassNode, sourceUnit),
                "@Translatable annotation should be applied over domain class"

        assert GrailsASTUtils.isDomainClass(translateWithClass, sourceUnit),
                "Translation class should be a domain class"

        println "Adding Translatable transform to ${ translatableClassNode.name }"

        addHasManyTranslations(translatableClassNode, translateWithClass)
        addNamedQueries(translatableClassNode)
        addProxyGetters(translatableClassNode, translateWithClass)

    }

    private void addNamedQueries(ClassNode classNode) {
        FieldNode namedQueriesField = classNode.getField(GrailsDomainClassProperty.NAMED_QUERIES)
        ClosureExpression namedQueriesClosure = namedQueriesField.initialExpression
        BlockStatement block = namedQueriesClosure.code

        Statement code = new AstBuilder().buildFromCode {
            includeTranslations { Collection<Locale> translations = null ->
                resultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
                createAlias('translations', 't', org.hibernate.criterion.CriteriaSpecification.LEFT_JOIN)

                if (translations) {
                    or {
                        isEmpty('translations')
                        'in'('t.locale', translations)
                    }
                }
            }
        }.pop()

        block.addStatement(code)
    }

    private void addHasManyTranslations(ClassNode classNode, ClassNode translationClass) {
        def hasManyField = classNode.getField(GrailsDomainClassProperty.HAS_MANY)
        def hasManyMap = hasManyField.initialExpression

        hasManyMap.addMapEntryExpression(
                new ConstantExpression('translations'),
                new ClassExpression(translationClass));
    }

    private void addProxyGetters(ClassNode classNode, ClassNode translationClass) {
        def translatableFields = getTranslatableFields(translationClass)
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
