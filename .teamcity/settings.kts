import jetbrains.buildServer.configs.kotlin.v2019_2.*
import java.io.File

version = "2020.1"

project {

    /* Read yml files to create subprojects */
    var projectsRoot = File(DslContext.baseDir, "/Projects")
    addProject(projectsRoot, this)
}


/*
* Recursively adds projects to match the file system
*/
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

            addProject(item, sub)
        }
    }
}