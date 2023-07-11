package cd.ghost.myapplication.data.source.local

import cd.ghost.myapplication.data.Result
import cd.ghost.myapplication.data.Task
import cd.ghost.myapplication.data.source.TaskDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskLocalDataSource(
    private val tasksDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {

    override suspend fun getTasks(): Result<List<Task>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(tasksDao.getTasks())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getTask(taskId: String): Result<Task> =
        withContext(ioDispatcher) {
            return@withContext try {
                val task = tasksDao.getTaskById(taskId)
                if (task != null) {
                    return@withContext Result.Success(task)
                } else {
                    return@withContext Result.Error(Exception("Task not found!"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: String) {
        tasksDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) {
        tasksDao.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTask() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTask() = withContext(ioDispatcher) {
        tasksDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteTaskById(taskId)
    }
}