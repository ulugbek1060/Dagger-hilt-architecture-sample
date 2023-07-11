package cd.ghost.myapplication.data.source

import cd.ghost.myapplication.data.Task

interface TaskRepository {

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTask()

    suspend fun deleteAllTask()

    suspend fun deleteTask(taskId: String)

}