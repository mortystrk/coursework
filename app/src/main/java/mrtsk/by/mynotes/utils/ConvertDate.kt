package mrtsk.by.mynotes.utils

fun ConvertDate(date: String) : String {
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