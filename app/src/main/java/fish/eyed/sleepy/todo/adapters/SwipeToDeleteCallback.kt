package fish.eyed.sleepy.todo.adapters

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import fish.eyed.sleepy.todo.R
import fish.eyed.sleepy.todo.data.SimpleTodoRepository

abstract class SwipeToDeleteTodoCallback(context: Context, direction: Int) :
    ItemTouchHelper.SimpleCallback(0, direction) {

    private val deleteIcon = context.getDrawable(R.drawable.ic_delete_black_24dp)
    private val deleteIconIntrinsicWidth = deleteIcon?.intrinsicWidth
    private val deleteIconIntrinsicHeight = deleteIcon?.intrinsicHeight

    private val background = ColorDrawable()
    private val leftBackgroundColor = Color.parseColor("#FF5454")
//    private val leftBackgroundColor = context.getDrawable(R.color.swipeToDeleteBackground)
//    private val rightBackgroundColor = Color.parseColor("#25AA71")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        val isCanceled = dX == 0f && !isCurrentlyActive
        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the red delete background
        val isLeftDirection = dX < 0
        if (isLeftDirection) {
            background.color = leftBackgroundColor
            background.setBounds(
                itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom
            )
        } else {
            background.color = leftBackgroundColor
            background.setBounds(
                itemView.left,
                itemView.top, itemView.left + dX.toInt(), itemView.bottom
            )
        }
        background.draw(c)

        val itemHeight = itemView.bottom - itemView.top
        if (deleteIcon != null
            && deleteIconIntrinsicWidth != null
            && deleteIconIntrinsicHeight != null
        ) {

            if (isLeftDirection) {
                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - deleteIconIntrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - deleteIconIntrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - deleteIconIntrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + deleteIconIntrinsicHeight

                // Draw the delete icon
                deleteIcon.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon.draw(c)
            } else {
                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - deleteIconIntrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - deleteIconIntrinsicHeight) / 2
                val deleteIconLeft = itemView.left + deleteIconMargin
                val deleteIconRight = itemView.left + (deleteIconMargin + deleteIconIntrinsicWidth)
                val deleteIconBottom = deleteIconTop + deleteIconIntrinsicHeight

                // Draw the delete icon
                deleteIcon.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteIcon.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}