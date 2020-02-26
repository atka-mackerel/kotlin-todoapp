package fish.eyed.sleepy.todo.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import fish.eyed.sleepy.todo.utils.DateUtil
import java.util.*

private const val ARG_PARAM_DATE = "date"

class DatePickerDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val cal: Calendar = arguments?.let { args ->
            args.getString(ARG_PARAM_DATE)?.let { arg -> DateUtil.toDateCalendar(arg) }
        } ?: Calendar.getInstance()

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

//        return super.onCreateDialog(savedInstanceState)
        val context = requireActivity()
        val parent = requireParentFragment()
        if (parent !is DatePickerDialog.OnDateSetListener) {
            throw RuntimeException("$context must implement DatePickerDialog.OnDateSetListener")
        }
        return DatePickerDialog(context, parent, year, month, day)
    }

    companion object {
        @JvmStatic
        fun newInstance(date: String?): DatePickerDialogFragment {
            return DatePickerDialogFragment().apply {
                arguments = Bundle().also {
                    it.putString(ARG_PARAM_DATE, date)
                }
            }
        }
    }

}