package mrtsk.by.mynotes.core.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_private_notes.*

import mrtsk.by.mynotes.R
import mrtsk.by.mynotes.core.model.Notes
import mrtsk.by.mynotes.database.entities.PNote

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PrivateNotes.OnPrivateNotesFragmentListener] interface
 * to handle interaction events.
 * Use the [PrivateNotes.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class PrivateNotes : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listenerPrivateNotes: OnPrivateNotesFragmentListener? = null

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

        val view = inflater.inflate(R.layout.fragment_private_notes, container, false)
        recyclerView = view.findViewById(R.id.rv_private_notes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        when {
            Notes.pNotes.size == 0 -> {
                val adapter = PrivateNotesAdapter(Notes.pNotes, this.context!!)
                recyclerView.adapter = adapter
                isEmptyDataSet = true
            }
            Notes.pNotes.size == 1 -> {
                val adapter = PrivateNotesAdapter(Notes.pNotes, this.context!!)
                adapter.setOnItemClickListener(object : PrivateNotesAdapter.ClickListener {
                    override fun onItemClick(position: Int, view: View) {
                        onItemClick(position)
                    }
                })
                recyclerView.adapter = adapter
                isEmptyDataSet = false
            }
            else -> {
                val adapter = PrivateNotesAdapter(Notes.pNotes.reversed() as ArrayList<PNote>, this.context!!)
                adapter.setOnItemClickListener(object : PrivateNotesAdapter.ClickListener {
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

        fab_add_private_note.setOnClickListener {
            listenerPrivateNotes?.onFABPressed()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    private fun onDataSetIsEmpty() {
        listenerPrivateNotes?.onDataSetIsEmptyPrivateNotes()
    }

    private fun onItemClick(position: Int) {
        listenerPrivateNotes?.onItemClickPrivateNotes(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPrivateNotesFragmentListener) {
            listenerPrivateNotes = context
        } else {
           // throw RuntimeException(context.toString() + " must implement OnPrivateNotesFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerPrivateNotes = null
    }

    interface OnPrivateNotesFragmentListener {
        fun onDataSetIsEmptyPrivateNotes()
        fun onItemClickPrivateNotes(position: Int)
        fun onFABPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrivateNotes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class PrivateNotesAdapter(private var notes : ArrayList<PNote>, val context: Context):
        RecyclerView.Adapter<PrivateNotesAdapter.ContentViewHolder>() {

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
            //return ContentViewHolder(LayoutInflater.from(context).inflate(R.layout.content_item, parent, false))
        }

        override fun getItemCount(): Int {
            return notes.size
        }

        override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
            holder.category_image.background = context.resources.getDrawable(R.drawable.rounded_corner_lock_category, null)
            Glide.with(context)
                .asBitmap()
                .load(R.drawable.baseline_lock_white_48dp)
                .into(holder.category_image)

            if (notes[position].title!!.length < 21) {
                holder.title.text = notes[position].title
            } else {
                holder.title.text = "${notes[position].title!!.substring(0, 21)}..."
            }

            if (notes[position].text!!.length < 21) {
                holder.text.text = notes[position].text
            } else {
                holder.text.text = "${notes[position].text!!.substring(0, 21)}..."
            }

            holder.date_text.text = replaceDate(notes[position].date!!)
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
            PrivateNotesAdapter.clickListener = clickListener
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
