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
class TranslatableTransformation implements ASTTransformation {

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
        GroobalizeASTUtils.addNamedQuery(classNode, code)
    }

    private void addHasManyTranslations(ClassNode classNode, ClassNode translationClass) {
        GroobalizeASTUtils.addHasManyRelationship(classNode, 'translations', translationClass)
    }

    private void addProxyGetters(ClassNode classNode, ClassNode translationClass) {
        def translatableFields = getTranslatableFields(translationClass)
        translatableFields.each { field ->
            String fieldName = field.name
            String getterName = GrailsClassUtils.getGetterName(fieldName)
            def getterCode = new AstBuilder().buildFromString("""
                com.ticketbis.groobalize.GroobalizeHelper.
                    getPreferredTranslation(translations)?."$fieldName"
            """).last()

            def methodNode = new MethodNode(
                    getterName,
                    FieldNode.ACC_PUBLIC,
                    field.type,
                    Parameter.EMPTY_ARRAY,
                    ClassHelper.EMPTY_TYPE_ARRAY,
                    getterCode)

            classNode.addMethod(methodNode)
            GroobalizeASTUtils.addTransient(classNode, fieldName)
        }
    }

    private List<FieldNode> getTranslatableFields(ClassNode translationClass) {
        translationClass.fields.findAll {
            !(it.modifiers & FieldNode.ACC_STATIC) &&
            !(it.name in NON_TRANSLATABLE_FIELDS)
        }
    }

}
