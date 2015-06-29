import grails.orm.HibernateCriteriaBuilder
import com.ticketbis.groobalize.HibernateCriteriaBuilderExtension

class GroobalizeGrailsPlugin {
    def version = "0.1.2"
    // the plugin version
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/domain/**"
    ]

    // TODO Fill in these fields
    def title = "Groobalize Plugin" // Headline display name of the plugin
    def author = "Endika Gutiérrez"
    def authorEmail = ""
    def description = '''\
Internacionalization plugin for grails inspired by Gloobalize
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/groobalize"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Ticketbis", url: "http://engineering.ticketbis.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/ticketbis/grails-groobalize" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        HibernateCriteriaBuilder.mixin(HibernateCriteriaBuilderExtension)
    }

    def doWithApplicationContext = { ctx ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
