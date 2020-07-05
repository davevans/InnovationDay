import jetbrains.buildServer.configs.kotlin.v2019_2.*
import java.io.File

version = "2020.1"

project {

    /* Read yml files to create subprojects */
    val projectsRoot = File(DslContext.baseDir, "/Projects")
    addProject(projectsRoot, this)


}


/*
* Recursively adds projects to match the file system
*/
fun addProject(currentDirectory: File, parent: Project) : Unit {
    val subdirectories = currentDirectory.listFiles()

    subdirectories?.forEach{
        if(it.isDirectory)
        {
            val projectDto : ProjectDto = YAMLUtil.parseDto("_Project.yaml", ProjectDto::class)
            //TODO:

            val projectName = projectDto.name
            val sub = parent.subProject {
                id(projectName)
                name = projectName
            }
            addProject(it, sub)
        }
    }

}