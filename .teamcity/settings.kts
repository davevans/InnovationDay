import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import java.io.File
import java.util.*

//data class TeamCityProject(val id: String, val name: String, val parentProjectName: String?)

// Model
class TeamCityProject(val name: String) {
    //var parent: TeamCityProject? = null
}

version = "2020.1"

project {

    /* Read yml files to create subprojects */
    var projectsRoot = File(DslContext.baseDir, "/Projects")
    addProject(projectsRoot, this)
}

fun addProject(currentDirectory: File, parent: Project) : Unit {


    var subdirectories = currentDirectory.listFiles()
    for (item: File in subdirectories) {
        if(item.isDirectory())
        {
            var projectName = item.name;
            var sub = parent.subProject({
                id(projectName)
                name = projectName
            })

            println("added $projectName")

            addProject(item, sub)
        }
    }
}

