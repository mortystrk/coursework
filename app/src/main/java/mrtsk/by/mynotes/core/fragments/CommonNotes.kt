package mrtsk.by.mynotes.core.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.R.id.rv_common_notes
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.database.entities.Note

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CommonNotes.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommonNotes.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommonNotes : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var recyclerView: RecyclerView
    private var isEmptyDataSet = false

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

        val view = inflater.inflate(R.layout.fragment_common_notes, container, false)
        recyclerView = view.findViewById(R.id.rv_common_notes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        when {
            Notes.notes.size == 0 -> {
                val adapter = CommonNotesAdapter(Notes.notes, this.context!!)
                recyclerView.adapter = adapter
                isEmptyDataSet = true
            }
            Notes.notes.size == 1 -> {
                val adapter = CommonNotesAdapter(Notes.notes, this.context!!)
                adapter.setOnItemClickListener(object : CommonNotesAdapter.ClickListener {
                    override fun onItemClick(position: Int, view: View) {
                        onItemClick(position)
                    }
                })
                recyclerView.adapter = adapter
                isEmptyDataSet = false
            }
            else -> {
                val adapter = CommonNotesAdapter(Notes.notes.reversed() as ArrayList<Note>, this.context!!)
                adapter.setOnItemClickListener(object : CommonNotesAdapter.ClickListener {
                    override fun onItemClick(position: Int, view: View) {
                        onItemClick(position)
                    }
                })
                recyclerView.adapter = adapter
                isEmptyDataSet = false
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isEmptyDataSet)
        onDataSetIsEmpty()
    }

    // TODO: Rename method, update argument and hook method into UI event
    private fun onDataSetIsEmpty() {
        listener?.onDataSetIsEmpty()
    }

    private fun onItemClick(position: Int) {
        listener?.onItemClick(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
           // throw RuntimeException(context.toString() + " must implement OnCreateNoteListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onDataSetIsEmpty()
        fun onItemClick(position: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommonNotes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class CommonNotesAdapter(private var notes : ArrayList<Note>, val context: Context):
        RecyclerView.Adapter<CommonNotesAdapter.ContentViewHolder>() {

        companion object {
            lateinit var clickListener: ClickListener
            private const val EMPTY_PLACEHOLDER = 0
            private const val NORMAL_PLACEHOLDER = 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (notes.size == 0) {
                EMPTY_PLACEHOLDER
            } else {
                NORMAL_PLACEHOLDER
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ContentViewHolder {
            return when (p1) {
                EMPTY_PLACEHOLDER -> ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.empty_fragment, parent, false))
                NORMAL_PLACEHOLDER -> ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.content_item, parent, false))
                else -> {
                    return ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.empty_fragment, parent, false))
                }
            }

           // return ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.content_item, parent, false))
        }

        override fun getItemCount(): Int {
            return notes.size
        }

        override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {

            when (notes[position].category) {
                "Work" -> {
                    holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_work_category, null)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.baseline_work_white_48)
                        .into(holder.category_image)
                }
                "Education" -> {
                    holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_edu_category, null)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.baseline_school_white_48)
                        .into(holder.category_image)
                }
                "Home" -> {
                    holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_home_category, null)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.baseline_home_white_48)
                        .into(holder.category_image)
                }
                "Hobby" -> {
                    holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_hobby_category, null)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.baseline_palette_white_48)
                        .into(holder.category_image)
                }
                "Pet" -> {
                    holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_pet_category, null)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.baseline_pets_white_48)
                        .into(holder.category_image)
                }
            }

            holder.title.text = notes[position].title
            holder.date_text.text = replaceDate(notes[position].date!!)

            if (notes[position].text!!.length < 21) {
                holder.text.text = notes[position].text
            } else {
                holder.text.text = "${notes[position].text!!.substring(0, 21)}..."
            }
        }


        class ContentViewHolder (itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            internal var date_text: TextView = itemView.findViewById(R.id.tv_date) as TextView
            internal var title: TextView = itemView.findViewById(R.id.tv_title) as TextView
            internal var category_image: ImageView = itemView.findViewById(R.id.iv_category) as ImageView
            internal  var text: TextView = itemView.findViewById(R.id.tv_item_text) as TextView

            override fun onClick(v: View?) {
                clickListener.onItemClick(adapterPosition, v!!)
            }
        }

        interface ClickListener {
            fun onItemClick(position: Int, view: View)
        }

        fun setOnItemClickListener(clickListener: ClickListener) {
            CommonNotesAdapter.clickListener = clickListener
        }

        private fun replaceDate(date: String) : String {
            val month = date.substring(3, 5)

            val monthInWord = when (month) {
                "01" -> "Января"
                "02" -> "Февраля"
                "03" -> "Марта"
                "04" -> "Апреля"
                "05" -> "Мая"
                "06" -> "Июня"
                "07" -> "Июля"
                "08" -> "Августа"
                "09" -> "Сентября"
                "10" -> "Октября"
                "11" -> "Ноября"
                "12" -> "Декабря"
                else -> "Что?"
            }

            val day = date.substring(0, 2)

            return "$day $monthInWord"
        }
    }
}
