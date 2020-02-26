package fish.eyed.sleepy.todo.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import fish.eyed.sleepy.todo.utils.DateUtil
import java.util.*

private const val ARG_PARAM_TIME = "time"

class TimePickerDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val cal: Calendar = arguments?.let { args ->
            args.getString(ARG_PARAM_TIME)?.let { arg -> DateUtil.toTimeCalendar(arg) }
        } ?: Calendar.getInstance()

        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val context = requireActivity()
        val parent = requireParentFragment()
        if (parent !is TimePickerDialog.OnTimeSetListener) {
            throw RuntimeException("$context must implement TimePickerDialog.OnTimeSetListener")
        }
        return TimePickerDialog(context, parent, hour, minute, true)
    }

    companion object {
        @JvmStatic
        fun newInstance(date: String?): TimePickerDialogFragment {
            return TimePickerDialogFragment().apply {
                arguments = Bundle().also {
                    it.putString(ARG_PARAM_TIME, date)
                }
            }
        }
    }

}