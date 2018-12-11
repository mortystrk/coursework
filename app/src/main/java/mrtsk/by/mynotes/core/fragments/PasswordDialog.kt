package mrtsk.by.mynotes.core.fragments


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.synthetic.main.password_dialog_fragment.*

import mrtsk.by.mynotes.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mListener: PasswordDialog.PasswordListener


class PasswordDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.password_dialog_fragment, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        b_back_pass_dialog.setOnClickListener {
            dialog.cancel()
        }
        b_ok_pass_dialog.setOnClickListener{
            mListener.onPositiveClick(et_password_dialog.text.toString())
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is PasswordListener) {
            mListener = context
        } else {
            // throw RuntimeException(context.toString() + " must implement OnCreateNoteListener")
        }
    }

    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater

        builder.setView(inflater.inflate(R.layout.password_dialog_fragment, null))
            .setPositiveButton("Далее") { _: DialogInterface, _: Int ->
                mListener.onPositiveClick(et_password_dialog.text.toString())
            }

        return builder.create()
    }*/



    interface PasswordListener {
        fun onPositiveClick(password: String)
    }
}
