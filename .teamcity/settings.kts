import com.charleskorn.kaml.Yaml
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import java.io.File
import java.util.*
import java.io.Serializable
//import kotlinx.serialization.*
//import kotlinx-ser

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

data class TeamCityProject(val name: String )

version = "2020.1"

project {
    template(MyTemplate)
    buildType(HelloWorld)



    /* Read yml files to create subprojects */
    var projectsRoot = File(DslContext.baseDir, "/Projects")
    var subProjects = discoverSubProjects(projectsRoot)
    for (p: Project in subProjects) {
        subProject(p)
    }
}

fun discoverSubProjects(dir: File) : List<Project> {
    var result = LinkedList<Project>()
    var projects = LinkedList<File>()
    var subdirectories = dir.listFiles()
    for (item: File in subdirectories) {
        if(item.isDirectory())
        {
            projects.add(item);
            var projectFile = getProjectFile(item);
            if(projectFile != null)
            {
                var subProject = addProject(projectFile, null)
                result.add(subProject)
            }
        }
    }

    while (!projects.isEmpty())
    {
        val current: File = projects.poll()
        subdirectories = current.listFiles()
        for (item: File in subdirectories) {
            if(item.isDirectory())
            {
                projects.add(item);
                var projectFile = getProjectFile(item);
            }
        }
    }
    return result
}

fun getProjectFile(dir: File) : File? {
    if(!dir.isDirectory())
        return null

    val projectFile = dir.listFiles { f -> f.isFile() && f.name == "_Project.yml" }.firstOrNull();
    if(projectFile != null)
        return projectFile

    return null
}

fun addProject(file: File, parentProjectName: String?) : Project {
    var fileContent = file.readText()
    var sub = Project({
        id(file.parentFile.name)
        name = file.parentFile.name
    })
    println("Creating subProject with name ${sub.name}.")
    return sub

    //var serializer = TeamCityProject.serializer()
    //var project: TeamCityProject = Yaml.default.parse(TeamCityProject.serializer(), fileContent)
}

fun CreateSubProject(f: File) : Unit {

    //var x = f.
}


//@Serializable
data class Data(val a: Int, val b: Int)


object HelloWorld: BuildType({
    name = "Hello World"
    templates(MyTemplate)

    params {
        param("repository", "Hello World")
    }
})

object MyTemplate: Template({
    name = "MyTemplate"

    steps  {
        script {
            scriptContent = "echo 'hello world' lets compile %repository%"
        }
    }

    params {
        param("repository", "")
    }
})