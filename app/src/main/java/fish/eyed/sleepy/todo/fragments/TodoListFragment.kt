package fish.eyed.sleepy.todo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.adapters.SwipeToDeleteTodoCallback
import fish.eyed.sleepy.todo.adapters.TabAdapter
import fish.eyed.sleepy.todo.adapters.TodoListAdapter
import fish.eyed.sleepy.todo.data.SimpleTodoRepository
import fish.eyed.sleepy.todo.data.TodoRepository
import fish.eyed.sleepy.todo.models.Todo
import kotlin.properties.Delegates

private const val ARG_PARAM_POSITION = "position"
private const val ARG_PARAM_VIEW_MODE = "viewMode"

class TodoListFragment(private val mAdapterInteractionListener: OnAdapterInteractionListener) :
    Fragment() {

    private var mPosition: Int by Delegates.notNull()
    private lateinit var mViewMode: ViewMode
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: TodoListAdapter
    private lateinit var mFragmentInteractionListener: OnFragmentInteractionListener

    private val mProgressBar: ProgressBar by lazy {
        requireActivity().findViewById<ProgressBar>(R.id.progressBar)
    }

    private val mTodoRepository: TodoRepository = SimpleTodoRepository.getInstance()

    override fun onPause() {
        super.onPause()
        Log.d("$this", "onPause")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("$this", "onCreate")
        arguments?.also {
            mPosition = it.getInt(ARG_PARAM_POSITION)
            mViewMode = it.getSerializable(ARG_PARAM_VIEW_MODE) as ViewMode
        }

        mFragmentInteractionListener = requireParentFragment().let {
            if (it is OnFragmentInteractionListener) {
                it
            } else {
                throw RuntimeException("$context must implement OnItemInteractionListener")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("$this", "onCreateView")
        return inflater.inflate(R.layout.fragment_tab_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("$this", "onViewCreated")
        setupView(view)
        fetchDataSet()
    }

    private fun setupView(view: View) {
        // レイアウトマネージャ
        val linearLayoutManager = LinearLayoutManager(requireContext())

        // dividerの設定
//        val divItemDecoration =
//            DividerItemDecoration(context, linearLayoutManager.orientation).apply {
//                setDrawable(context?.getDrawable(R.drawable.divider) as Drawable)
//            }

        // アダプターの設定
        mAdapter = TodoListAdapter(mutableListOf()).also { adapter ->
            // 行クリック時の挙動
            adapter.setOnItemClickListener(View.OnClickListener { view ->
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(
                        R.id.main_container,
                        TodoEditFragment.newInstance((view.tag as Todo).id)
                    )
                    .commit()
            })

            adapter.setOnItemLongClickListener(View.OnLongClickListener { view ->
                val todo: Todo = view.tag as Todo
                todo.id?.let { id ->
                    val filter = mutableMapOf<String, Any?>(Pair("done", true))
                    mTodoRepository.update(id, filter,
                        { mFragmentInteractionListener.onItemDone(mPosition, mAdapter) },
                        { e ->
                            Log.d("$this", "${e.message}")
                            Toast.makeText(
                                requireContext(),
                                R.string.message_update_failure,
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        null
                    )
                }
                true
            })
        }

        mAdapterInteractionListener.onAdapterSetup(mPosition, mAdapter)

        mRecyclerView = view.findViewById<RecyclerView>(R.id.todo_view).also {
            it.layoutManager = linearLayoutManager
            it.adapter = mAdapter
        }

        val swipeDirection = when (mPosition) {
            TabAdapter.POSITION_TODO -> ItemTouchHelper.RIGHT
            else -> ItemTouchHelper.LEFT
        }

        val swipeHandler = object :
            SwipeToDeleteTodoCallback(requireContext(), swipeDirection) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d("$this", "onSwiped")
                val todo = mAdapter[viewHolder.adapterPosition]
                todo.id ?: return
                mTodoRepository.delete(
                    todo.id,
                    { mAdapter.removeItem(viewHolder.adapterPosition) },
                    { e ->
                        Log.d("$this", "${e.message}")
                        mAdapter.notifyDataSetChanged()
                    },
                    null)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    override fun onResume() {
        super.onResume()
        Log.d("$this", "onResume")
//        loadDataSet()
    }

    override fun onStop() {
        super.onStop()
        Log.d("$this", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("$this", "onDestroyView")
        mAdapterInteractionListener.onDestroyView(mPosition)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("$this", "onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("$this", "onDetach")
    }

    private fun fetchDataSet() {
        mProgressBar.visibility = ProgressBar.VISIBLE
        mTodoRepository.fetch(
            mutableMapOf(Pair("done", mViewMode == ViewMode.DONE)),
            { list ->
                mAdapter.replaceItems(list)
                mAdapter.notifyDataSetChanged()
            },
            { e ->
                Log.d("load", "$e")
                Toast.makeText(requireContext(), R.string.message_find_failure, Toast.LENGTH_SHORT)
                    .show()
            },
            this::dismissProgressBar
        )
    }

    private fun dismissProgressBar() {
        mProgressBar.visibility = ProgressBar.GONE
    }

    interface OnFragmentInteractionListener {
        fun onItemDone(position: Int, adapter: TodoListAdapter)
        fun onItemDeleted(position: Int, adapter: TodoListAdapter)
    }

    interface OnAdapterInteractionListener {
        fun onAdapterSetup(position: Int, adapter: TodoListAdapter)
        fun onDestroyView(position: Int)
    }

    enum class ViewMode {
        TODO,
        DONE
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, viewMode: ViewMode, listener: OnAdapterInteractionListener) =
            TodoListFragment(listener).also {
                it.arguments = Bundle().also { arg ->
                    arg.putInt(ARG_PARAM_POSITION, position)
                    arg.putSerializable(ARG_PARAM_VIEW_MODE, viewMode)
                }
            }
    }
}