package fish.eyed.sleepy.todo.models

import com.google.firebase.Timestamp
import java.io.Serializable

class Todo(
    val id: String?,
    val title: String,
    val detail: String,
    val done: Boolean,
    var limit: Timestamp?
) : Serializable {
    fun toMap(): MutableMap<String, Any?> = mutableMapOf<String, Any?>().apply {
        put("id", id)
        put("title", title)
        put("detail", detail)
        put("done", done)
        put("limit", limit)
    }
}