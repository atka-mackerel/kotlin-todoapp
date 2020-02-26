package fish.eyed.sleepy.todo.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import fish.eyed.sleepy.todo.models.Todo
import fish.eyed.sleepy.todo.utils.DateUtil
import fish.eyed.sleepy.todo.ext.itself
import java.lang.Exception

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    var id: String? = null
    var title: MutableLiveData<String> = MutableLiveData()
    var detail: MutableLiveData<String> = MutableLiveData()
    var date: MutableLiveData<String> = MutableLiveData()
    var time: MutableLiveData<String> = MutableLiveData()
    var done: MutableLiveData<Boolean> = MutableLiveData()

    fun fetch(
        id: String,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        repository.fetchOne(
            id,
            { todo ->
                this.id = todo.id
                title.value = todo.title
                detail.value = todo.detail
                done.value = todo.done
                todo.limit?.toDate()?.let {
                    date.value = DateUtil.formatDate(it)
                    time.value = DateUtil.formatTime(it)
                }
            },
            onFailure,
            onComplete
        )
    }

    fun toDomainObject(): Todo? {
        val title: String = title.value?.itself() ?: return null
        val detail: String = detail.value?.itself() ?: return null
        val datetime: Timestamp? = date.value?.let { d ->
            time.value?.let { t ->
                Timestamp(DateUtil.toDateTime("$d $t"))
            }
        }
        val done: Boolean = done.value ?: false
        return Todo(id, title, detail, done, datetime)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            TodoViewModel(repository) as T
    }
}