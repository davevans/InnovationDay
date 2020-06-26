import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import java.io.File
import java.util.*

data class TeamCityProject(val id: String, val name: String, val parentProjectName: String?)

version = "2020.1"

project {

    //template(MyTemplate)
    //buildType(HelloWorld)


    /* Read yml files to create subprojects */
    var projectsRoot = File(DslContext.baseDir, "/Projects")
    var subProjects = discoverSubProjects(projectsRoot)
    for (p: TeamCityProject in subProjects) {

        println("Adding project ${p.name}. ParentName: ${p.parentProjectName}")
        //var id = id(p.id);
        //var parentId = if(p.parentProjectName != null) AbsoluteId(p.parentProjectName.toId()) else null

        subProject({
            id(p.id)
            name = p.name
            uuid = p.name
        })
    }
}

fun discoverSubProjects(dir: File) : List<TeamCityProject> {
    var result = LinkedList<TeamCityProject>()
    var projects = LinkedList<File>()
    var subdirectories = dir.listFiles()
    for (item: File in subdirectories) {
        if(item.isDirectory())
        {
            projects.add(item);
            var projectFile = getProjectFile(item);
            if(projectFile != null)
            {
                var subProject = getTeamCityProject(projectFile, null)
                result.add(subProject)
            }
        }
    }

    while (!projects.isEmpty())
    {
        val parentDirectory: File = projects.poll()
        var parentProjFile = getProjectFile(parentDirectory);

        var parentTeamCityProj = getTeamCityProject(parentProjFile!!, null)
        subdirectories = parentDirectory.listFiles()
        for (item: File in subdirectories) {
            if(item.isDirectory())
            {
                projects.add(item);
                var projectFile = getProjectFile(item);
                if(projectFile != null)
                {
                    var subProject = getTeamCityProject(projectFile, parentTeamCityProj.name)
                    result.add(subProject)
                }
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

fun getTeamCityProject(file: File, parentProjectName: String?) : TeamCityProject {
    var fileContent = file.readText()
    /*
    var sub = Project({
        id(file.parentFile.name)
        name = file.parentFile.name
    })
    */

    var name = file.parentFile.name
    var teamCityProj = TeamCityProject(name, name, parentProjectName)

    //println("Creating subProject with name ${sub.name}.")
    return teamCityProj

    //var serializer = TeamCityProject.serializer()
    //var project: TeamCityProject = Yaml.default.parse(TeamCityProject.serializer(), fileContent)
}


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