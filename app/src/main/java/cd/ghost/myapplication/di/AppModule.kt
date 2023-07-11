package cd.ghost.myapplication.di

import android.content.Context
import androidx.room.Room
import cd.ghost.myapplication.data.source.TasksDataSource
import cd.ghost.myapplication.data.source.local.TasksLocalDataSource
import cd.ghost.myapplication.data.source.local.ToDoDatabase
import cd.ghost.myapplication.data.source.remote.TasksRemoteDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TaskRemoteDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TaskLocalDataSource

    @JvmStatic
    @Singleton
    @TaskRemoteDataSource
    @Provides
    fun provideTaskRemoteDataSource(): TasksDataSource {
        return TasksRemoteDataSource
    }

    @JvmStatic
    @Singleton
    @TaskLocalDataSource
    @Provides
    fun provideTaskLocalDataSource(
        database: ToDoDatabase,
        ioDispatcher: CoroutineDispatcher
    ): TasksDataSource {
        return TasksLocalDataSource(
            tasksDao = database.taskDao(),
            ioDispatcher = ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppDatabase(
        context: Context
    ): ToDoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "cd-ghost-toDo-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

}