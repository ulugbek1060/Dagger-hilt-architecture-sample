package cd.ghost.tasks

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import cd.ghost.common.base.BaseArgument
import cd.ghost.common.base.args
import cd.ghost.common.helper.setupRefreshLayout
import cd.ghost.common.helper.setupSnackbar
import cd.ghost.common.helper.viewBinding
import cd.ghost.tasks.databinding.FragmentTasksBinding
import cd.ghost.tasks.di.TasksSubcompProvider
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    class TasksArgument(
        val message: Int
    ) : BaseArgument

    private val TAG = "TasksFragment"

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<TasksViewModel> { factory }

    private val binding by viewBinding<FragmentTasksBinding>()

    private lateinit var adapter: TasksAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as TasksSubcompProvider)
            .provideTaskSubcomp()
            .create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TasksAdapter(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)

        val message = args<TasksArgument>()?.message
        viewModel.showEditResultMessage(message)

        binding.apply {
            tasksList.adapter = adapter
            // Set the lifecycle owner to the lifecycle of the view
            setupRefreshLayout(refreshLayout, tasksList)

            viewModel.items.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            refreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }

            viewModel.dataLoading.observe(viewLifecycleOwner) {
                refreshLayout.isRefreshing = it
            }

            viewModel.empty.observe(viewLifecycleOwner) {
                tasksLinearLayout.isVisible = !it
                noTasksLayout.isVisible = it
            }

            viewModel.noTaskIconRes.observe(viewLifecycleOwner) {
                noTasksIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), it))
            }

            viewModel.noTasksLabel.observe(viewLifecycleOwner) {
                noTasksText.text = getText(it)
            }

            viewModel.currentFilteringLabel.observe(viewLifecycleOwner) {
                filteringText.text = getText(it)
            }

            addTaskFab.setOnClickListener {
                Log.d(TAG, "onViewCreated: ")
                viewModel.addNewTask()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            cd.ghost.common.R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }

            cd.ghost.common.R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }

            cd.ghost.common.R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }

            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(cd.ghost.common.R.menu.tasks_fragment_menu, menu)
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(cd.ghost.common.R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(cd.ghost.common.R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        cd.ghost.common.R.id.active -> TasksFilterType.ACTIVE_TASKS
                        cd.ghost.common.R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                viewModel.loadTasks(false)
                true
            }
            show()
        }
    }
}