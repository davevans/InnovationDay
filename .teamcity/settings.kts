import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import java.io.File
import java.util.*

//data class TeamCityProject(val id: String, val name: String, val parentProjectName: String?)

// Model
class TeamCityProject(val name: String) {
    var parent: TeamCityProject? = null
}

version = "2020.1"

project {


    /* Read yml files to create subprojects */
    var projectsRoot = File(DslContext.baseDir, "/Projects")
    var subdirectories = projectsRoot.listFiles()
    for (item: File in subdirectories)
    {
        if(item.isDirectory())
        {
            var proj = getProject(item, null);
            println("adding ${proj!!.name}")

            subProject({
                id(proj!!.name)
                name = proj.name
            })
        }
    }
}

fun getProject(currentDirectory: File, parent: TeamCityProject?) : TeamCityProject? {

    var proj = TeamCityProject(currentDirectory.name)
    proj.parent = parent

    println("discovered ${proj.name}")

    var subdirectories = currentDirectory.listFiles()
    for (item: File in subdirectories) {
        if(item.isDirectory())
        {
            getProject(item, proj);
        }
    }

    return proj;
}

