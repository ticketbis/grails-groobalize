package com.ticketbis.groobalize.ast

import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class TranslatableTransformation implements ASTTransformation {

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
    }

    private void addHasManyTranslations(ClassNode classNode, ClassExpression translationClass) {
        def hasManyField = classNode.getField('hasMany')
        def hasManyMap = hasManyField.initialExpression

        hasManyMap.addMapEntryExpression(
                new ConstantExpression('translations'),
                translationClass);
    }

}
