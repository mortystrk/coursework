package mrtsk.by.mynotes.core.model

import mrtsk.by.mynotes.database.entities.Note
import mrtsk.by.mynotes.database.entities.PNote

class Notes {

    companion object {
        var notes: ArrayList<Note>? = null
        var pNotes: ArrayList<PNote>? = null
    }
}