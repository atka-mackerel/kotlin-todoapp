package fish.eyed.sleepy.todo.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fish.eyed.sleepy.todo.models.Todo

interface TodoRepository {

    fun fetchOne(
        id: String,
        onSuccess: (todo: Todo) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun fetch(
        onSuccess: (todoList: List<Todo>) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun fetch(
        condition: MutableMap<String, Any>,
        onSuccess: (todoList: List<Todo>) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun add(
        todo: Todo,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun update(
        id: String,
        fields: MutableMap<String, Any?>,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun update(
        id: String,
        todo: Todo,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    )
}

/**
 *
 */
class SimpleTodoRepository private constructor() : TodoRepository {

    companion object {
        private const val COLLECTION_PATH = "todo_list"
        private var instance: SimpleTodoRepository? = null

        @JvmStatic
        fun getInstance() = instance ?: SimpleTodoRepository()
    }

    /**
     *
     */
    override fun fetchOne(
        id: String,
        onSuccess: (todo: Todo) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        getCollection()
            .document(id)
            .get()
            .addOnFailureListener(onFailure)
            .addOnSuccessListener { data -> build(data).let(onSuccess) }
            .addOnCompleteListener { onComplete?.invoke() }
    }

    /**
     *
     */
    override fun fetch(
        onSuccess: (todoList: List<Todo>) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        fetch(mutableMapOf(), onSuccess, onFailure, onComplete)
    }

    /**
     *
     */
    override fun fetch(
        filter: MutableMap<String, Any>,
        onSuccess: (todoList: List<Todo>) -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        getCollection()
            .let {
                filter.entries.fold(it as Query) { acc, entry ->
                    acc.whereEqualTo(entry.key, entry.value)
                }
            }
            .get()
            .addOnFailureListener(onFailure)
            .addOnSuccessListener { result ->
                result
                    .map(this::build)
                    .let(onSuccess)
            }
            .addOnCompleteListener { onComplete?.invoke() }
    }

    /**
     *
     */
    override fun add(
        todo: Todo,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        getCollection()
            .add(todo)
            .addOnFailureListener(onFailure)
            .addOnSuccessListener { onSuccess() }
            .addOnCompleteListener { onComplete?.invoke() }
    }

    /**
     *
     */
    override fun update(
        id: String,
        fields: MutableMap<String, Any?>,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        getCollection()
            .document(id)
            .update(fields)
            .addOnFailureListener(onFailure)
            .addOnSuccessListener { onSuccess() }
            .addOnCompleteListener { onComplete?.invoke() }
    }

    /**
     *
     */
    override fun update(
        id: String,
        todo: Todo,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        update(id, todo.toMap(), onSuccess, onFailure, onComplete)
    }

    /**
     *
     */
    override fun delete(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit,
        onComplete: (() -> Unit)?
    ) {
        getCollection()
            .document(id)
            .delete()
            .addOnFailureListener(onFailure)
            .addOnSuccessListener { onSuccess() }
            .addOnCompleteListener { onComplete?.invoke() }
    }

    private fun getCollection(): CollectionReference =
        FirebaseFirestore.getInstance().collection(COLLECTION_PATH)

    fun build(data: DocumentSnapshot) = Todo(
        data.reference.id,
        data["title"] as String,
        data["detail"] as String,
        data["done"] as Boolean,
        data["limit"] as Timestamp?
    )

}