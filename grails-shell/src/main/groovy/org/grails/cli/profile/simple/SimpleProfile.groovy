package org.grails.cli.profile.simple

import groovy.transform.CompileStatic
import jline.console.completer.Completer

import org.grails.cli.profile.CommandLineHandler
import org.grails.cli.profile.Profile

@CompileStatic
class SimpleProfile implements Profile {
    File profileDir
    String name
    List<CommandLineHandler> commandLineHandlers = null
    
    public SimpleProfile(String name, File profileDir) {
        this.name = name
        this.profileDir = profileDir 
    }

    @Override
    public Iterable<Completer> getCompleters() {
        [new SimpleProfileCompleter(profile: this)]
    }

    @Override
    public Iterable<CommandLineHandler> getCommandLineHandlers() {
        if(commandLineHandlers == null) {
            commandLineHandlers = []
            Collection<File> commandFiles = findCommandFiles()
            SimpleCommandHandler commandHandler = createCommandHandler(commandFiles)
            commandHandler.initialize()
            commandLineHandlers << commandHandler
        }
        commandLineHandlers
    }    

    protected Collection<File> findCommandFiles() {
        File commandsDir = new File(profileDir, "commands")
        Collection<File> commandFiles = commandsDir.listFiles().findAll { File file ->
            file.isFile() && file.name ==~ /^.*\.(yml|json)$/
        }.sort(false) { File file -> file.name }
        return commandFiles
    }

    protected SimpleCommandHandler createCommandHandler(Collection<File> commandFiles) {
        return new SimpleCommandHandler(commandFiles: commandFiles, profile: this)
    }
}
