package com.ticketbis.groobalize

import grails.orm.HibernateCriteriaBuilder
import grails.plugins.*

class GroobalizeGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/controllers/**",
            "grails-app/views/**",
            "grails-app/domain/**"
    ]

    // TODO Fill in these fields
    def title = "Groobalize Plugin" // Headline display name of the plugin
    def author = "Endika Gutiérrez & Jose Gargallo"
    def authorEmail = ""
    def description = '''\
Internacionalization plugin for grails inspired by Gloobalize
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/groobalize"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Ticketbis", url: "http://engineering.ticketbis.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/ticketbis/grails-groobalize" ]

    Closure doWithSpring() { {->
            // TODO Implement runtime spring config (optional)
        }
    }

    void doWithDynamicMethods() {
        HibernateCriteriaBuilder.mixin(HibernateCriteriaBuilderExtension)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
