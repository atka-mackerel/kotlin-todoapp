package fish.eyed.sleepy.todo.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.data.SimpleTodoRepository
import fish.eyed.sleepy.todo.data.TodoRepository
import fish.eyed.sleepy.todo.data.TodoViewModel
import fish.eyed.sleepy.todo.databinding.FragmentTodoEditBinding
import fish.eyed.sleepy.todo.models.Todo
import fish.eyed.sleepy.todo.utils.DateUtil
import java.lang.Exception

private const val ARG_TODO_ID = "todoId"

/**
 *
 */
class TodoEditFragment : Fragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var mTodoId: String? = null
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mEditDateLimit: EditText
    private lateinit var mEditTimeLimit: EditText

    private lateinit var binding: FragmentTodoEditBinding

    private val mTodoRepository: TodoRepository = SimpleTodoRepository.getInstance()
    private val viewModel: TodoViewModel by lazy {
        ViewModelProviders.of(
            this,
            TodoViewModel.Factory(mTodoRepository)
        ).get(TodoViewModel::class.java)
    }

    private val mProgressBar: ProgressBar by lazy {
        requireActivity().findViewById<ProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("$this", "onCreate")
        arguments?.let { arg ->
            mTodoId = arg.getString(ARG_TODO_ID)
        }
        setHasOptionsMenu(true)
    }

    private fun saveData(todo: Todo, onSuccess: () -> Unit, onFailure: (e: Exception) -> Unit) {
        mTodoId?.also {
            mTodoRepository.update(it, todo, onSuccess, onFailure, null)
        } ?: also {
            mTodoRepository.add(todo, onSuccess, onFailure, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("$this", "onCreateView")
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todo_edit, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupView(view)
        fetch()
    }

    private fun fetch() {
        viewModel.id ?: mTodoId?.let { id ->
            mProgressBar.visibility = ProgressBar.VISIBLE
            viewModel.fetch(
                id,
                { e -> Log.d("$this", "${e.message}") },
                this::dismissProgresBar
            )
        }
    }

    private fun setupView(view: View) {
        // 期限（日付）
        mEditDateLimit = view.findViewById<EditText>(R.id.editDateLimit).apply {
            setOnClickListener {
                val fragment = DatePickerDialogFragment.newInstance(viewModel.date.value)
                fragment.show(childFragmentManager, "datePick")
            }
        }

        // 期限（時刻）
        mEditTimeLimit = view.findViewById<EditText>(R.id.editTimeLimit).apply {
            setOnClickListener {
                val fragment = TimePickerDialogFragment.newInstance(viewModel.time.value)
                fragment.show(childFragmentManager, "timePick")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("$this", "onAttach")
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("$this", "onDetach")
        mListener = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        view?.let {
            viewModel.date.value = DateUtil.formatDate(year, month + 1, dayOfMonth)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        view?.let {
            viewModel.time.value = DateUtil.formatTime(hourOfDay, minute)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(false)
        }
        inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_edit_complete).apply {
            setOnMenuItemClickListener {
                viewModel.toDomainObject()?.let { todo ->
                    saveData(todo,
                        {
                            Toast.makeText(
                                requireActivity(),
                                R.string.message_save_success,
                                Toast.LENGTH_LONG
                            ).show()
                            requireFragmentManager().popBackStackImmediate()
                        },
                        { e ->
                            Log.d("$this", "${e.message}")
                            Toast.makeText(
                                requireActivity(),
                                R.string.message_save_failure,
                                Toast.LENGTH_LONG
                            ).show()
                        })
                }
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                requireFragmentManager().popBackStackImmediate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dismissProgresBar() {
        mProgressBar.visibility = ProgressBar.GONE
    }

    /**
     *
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(fragment: TodoEditFragment)
    }

    companion object {

        @JvmStatic
        fun newInstance(todoId: String? = null) =
            TodoEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TODO_ID, todoId)
                }
            }
    }
}
