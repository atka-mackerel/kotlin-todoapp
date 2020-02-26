package fish.eyed.sleepy.todo.adapters

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.fragments.TodoListFragment
import fish.eyed.sleepy.todo.models.Todo

class TabAdapter(
    fm: FragmentManager,
    private val context: Context
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT),
    TodoListFragment.OnAdapterInteractionListener {

    companion object {
        const val POSITION_TODO = 0
        const val POSITION_DONE = 1
    }

    private val mAdapterMap: MutableMap<Int, TodoListAdapter>

    init {
        Log.d("$this", "init")
        mAdapterMap = mutableMapOf()
    }

    override fun getItem(position: Int): Fragment {
        Log.d("$this", "getItem[$position]")
        return when (position) {
            POSITION_TODO -> TodoListFragment.newInstance(
                position,
                TodoListFragment.ViewMode.TODO,
                this
            )
            POSITION_DONE -> TodoListFragment.newInstance(
                position,
                TodoListFragment.ViewMode.DONE,
                this
            )
            else -> throw RuntimeException("Invalid position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.tab_todo)
            else -> context.getString(R.string.tab_todo_done)
        }
    }

    override fun getCount(): Int {
        return 2
    }

    fun updateTodoDataSet(dataSet: List<Todo>) {
        updateDataSet(dataSet, POSITION_TODO)
    }

    fun updateDoneDataSet(dataSet: List<Todo>) {
        updateDataSet(dataSet, POSITION_DONE)
    }

    private fun updateDataSet(dataSet: List<Todo>, position: Int) {
        mAdapterMap[position]?.also {
            it.replaceItems(dataSet)
            it.notifyDataSetChanged()
        }
    }

    override fun onAdapterSetup(position: Int, adapter: TodoListAdapter) {
        Log.d("$this", "onAdapterSetup")
        mAdapterMap[position] = adapter
    }

    override fun onDestroyView(position: Int) {
        Log.d("$this", "onDestroyView")
        mAdapterMap.remove(position)
    }

    //    fun destroyAllItem(pager: ViewPager) {
//        (0..count).forEach { i ->
//            val fragment: Any = instantiateItem(pager, i)
//            destroyItem(pager, i, fragment)
//        }
//    }

//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
//        mAdapterMap.remove(position)
//    }
}