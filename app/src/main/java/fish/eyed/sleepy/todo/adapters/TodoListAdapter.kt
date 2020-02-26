package fish.eyed.sleepy.todo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.models.Todo
import fish.eyed.sleepy.todo.utils.DateUtil

class TodoListAdapter(private val items: MutableList<Todo>) :
    RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutView: View = itemView
        val titleView: TextView = itemView.findViewById(R.id.textTitle)
        val limitView: TextView = itemView.findViewById(R.id.textLimit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.view_todo_item, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.titleView.text = items[position].title
        holder.limitView.text = items[position].limit?.toDate()?.let {
            "${DateUtil.formatDate(it)}\n${DateUtil.formatTime(it)}"
        }
        holder.layoutView.tag = items[position]
        holder.layoutView.setOnClickListener(onClickListener)
        holder.layoutView.setOnLongClickListener(onLongClickListener)
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        this.onClickListener = listener
    }

    fun setOnItemLongClickListener(listener: View.OnLongClickListener) {
        this.onLongClickListener = listener
    }

    fun addItem(item: Todo): Int {
        items.add(item)
        val insertedIndex = items.size - 1
        notifyItemInserted(insertedIndex)
        return insertedIndex
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItems(items: Collection<Todo>) {
        items.forEach { addItem(it) }
    }

    fun clearItems() {
        for (it in (items.size - 1 downTo 0)) {
            removeItem(it)
        }
    }

    fun replaceItems(items: Collection<Todo>) {
        clearItems()
        addItems(items)
    }

    operator fun get(position: Int): Todo = items[position]

}
