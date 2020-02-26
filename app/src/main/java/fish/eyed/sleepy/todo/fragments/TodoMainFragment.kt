package fish.eyed.sleepy.todo.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.adapters.TabAdapter
import fish.eyed.sleepy.todo.adapters.TodoListAdapter
import fish.eyed.sleepy.todo.data.SimpleTodoRepository

private const val ARG_VIEW_MODE = "viewMode"

/**
 *
 */
class TodoMainFragment : Fragment(), TodoListFragment.OnFragmentInteractionListener {
    private var mViewMode: Int? = null
    private var mListener: OnFragmentInteractionListener? = null

    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mAdapter: TabAdapter

    private val mTodoRepository = SimpleTodoRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("$this", "onCreate")
        arguments?.let {
            mViewMode = it.getInt(ARG_VIEW_MODE)
        }
        // TabAdapter
        mAdapter = TabAdapter(childFragmentManager, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("$this", "onCreateView")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_todo_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("$this", "onViewCreated")

        // ViewPager
        mViewPager = view.findViewById<ViewPager>(R.id.view_pager).also {
            it.adapter = mAdapter
        }
        // TabLayout
        mTabLayout = view.findViewById<TabLayout>(R.id.todo_tab).also {
            it.setupWithViewPager(mViewPager)
        }

        // 追加ボタン
        view.findViewById<FloatingActionButton>(R.id.fab).also {
            it.setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, TodoEditFragment.newInstance())
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("$this", "onResume")
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(true)
        }
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onItemDeleted(position: Int, adapter: TodoListAdapter) {

    }

    override fun onItemDone(
        position: Int,
        adapter: TodoListAdapter
    ) {
        mTodoRepository.fetch(
            mutableMapOf(Pair("done", false)),
            { list ->
                mAdapter.updateTodoDataSet(list)
                Snackbar.make(requireView(), "タスクを完了しました。", Snackbar.LENGTH_LONG)
                    .setAction("元に戻す", null).show()
            },
            { e -> Log.d("$this", "${e.message}") },
            null
        )

        mTodoRepository.fetch(
            mutableMapOf(Pair("done", true)),
            { list -> mAdapter.updateDoneDataSet(list) },
            { e -> Log.d("$this", "${e.message}") },
            null
        )
    }

    /**
     *
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(fragment: TodoMainFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TodoMainFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
