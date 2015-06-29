import org.codehaus.gant.GantBinding

eventCompileStart = { GantBinding compileBinding ->
    resolveDependencies()

    ant.taskdef(name: 'precompileGroovyc', classname: 'org.codehaus.groovy.ant.Groovyc')
    try {
        def sourceDir = (compileBinding.variables.groobalizePluginDir?.absolutePath ?: basedir) + '/src/groovy'
        def destDir = grailsSettings.projectWorkDir.absolutePath + "/plugin-classes"

        ant.mkdir(dir: destDir)
        grailsConsole.addStatus "Precompiling Groobalize AST transforms..."
        ant.precompileGroovyc(destdir: destDir,
                classpathref: "grails.compile.classpath",
                encoding: projectCompiler.encoding,
                verbose: projectCompiler.verbose,
                listfiles: projectCompiler.verbose) {
            src(path: sourceDir)
        }
    } catch (Exception e) {
        grailsConsole.error("Could not precompile sources: " + e.class.name + ": " + e.message, e)
    }
}
