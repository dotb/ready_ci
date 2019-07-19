package tasks.maven

import com.squarepolka.readyci.taskrunner.BuildEnvironment
import com.squarepolka.readyci.taskrunner.TaskFailedException
import com.squarepolka.readyci.tasks.Task
import org.springframework.stereotype.Component

@Component
class JavaDependenciesMaven: Task() {

    val TASK_MAVEN_BUILD = "maven_install"

    override fun taskIdentifier(): String {
        return TASK_MAVEN_BUILD
    }

    @Throws(TaskFailedException::class)
    override fun performTask(buildEnvironment: BuildEnvironment) {
        executeCommand(arrayOf("mvn", "install"), buildEnvironment.projectPath)
    }

}