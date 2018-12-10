package mrtsk.by.mynotes.core.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_create_note.*
import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.database.entities.Note
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreateNote.OnCreateNoteListener] interface
 * to handle interaction events.
 * Use the [CreateNote.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CreateNote : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnCreateNoteListener? = null
    private var previousImageView: ImageView? = null
    private var previousTextView: TextView? = null
    private var category: String = ""
    private var isPrivate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    @SuppressLint("SimpleDateFormat")
    fun onButtonPressed() {
        var flag = false
        var note: Note? = null
        if (validateData()) {
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val title = et_add_title.text.toString()
            val text = et_note_text.text.toString()
            val date = sdf.format(Date())
            note = Note(0, title, text, date, category)
            flag = true
        }
        listener?.onCreateNote(note, flag)
    }

    @SuppressLint("ResourceAsColor")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        b_done.setOnClickListener { onButtonPressed() }

        iv_add_work_category.setOnClickListener { changeSelectedView(it as ImageView, tv_work_categ)
        category = "Work"}
        iv_add_edu_category.setOnClickListener { changeSelectedView(it as ImageView, tv_edu_categ)
        category = "Education"}
        iv_add_hobby_category.setOnClickListener { changeSelectedView(it as ImageView, tv_hobby_categ)
        category = "Hobby"}
        iv_add_home_category.setOnClickListener { changeSelectedView(it as ImageView, tv_home_categ)
        category = "Home"}
        iv_add_pet_category.setOnClickListener { changeSelectedView(it as ImageView, tv_pet_categ)
        category = "Pet"}

        /*tv_make_private.setOnClickListener {
            if (!isPrivate) {
                iv_make_private.translationZ = 25.0f
                it.translationZ = 25.0f
                isPrivate = true
            } else {
                iv_make_private.translationZ = 0.0f
                it.translationZ = 0.0f
                isPrivate = false
            }
        }*/

        iv_make_private.setOnClickListener {
            if (!isPrivate) {
                tv_make_private.translationZ = 25.0f
                it.translationZ = 25.0f
                isPrivate = true
            } else {
                tv_make_private.translationZ = 0.0f
                it.translationZ = 0.0f
                isPrivate = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCreateNoteListener) {
            listener = context
        } else {
           // throw RuntimeException(context.toString() + " must implement OnCreateNoteListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnCreateNoteListener {
        // TODO: Update argument type and name
        fun onCreateNote(note: Note?, flag: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateNote.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateNote().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun changeSelectedView(currImageView: ImageView, currTextView: TextView) {
        currImageView.translationZ = 25.0f
        currTextView.setTextColor(resources.getColor(R.color.colorTextSelected, null))

        if (previousImageView != null) {
            previousImageView!!.translationZ = 0.0f
        }
        if (previousTextView != null) {
            previousTextView!!.setTextColor(resources.getColor(R.color.colorTextNonSelected, null))
        }

        previousImageView = currImageView
        previousTextView = currTextView
    }

    private fun validateData() : Boolean {

        val title = et_add_title.text.toString()
        val text = et_note_text.text.toString()

        return !(title.isEmpty() || text.isEmpty() || category.isEmpty())
    }
}
