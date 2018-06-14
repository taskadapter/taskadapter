package com.taskadapter.web.configeditor.server

import java.text.NumberFormat
import java.util.Locale

import com.vaadin.data.util.converter.StringToLongConverter

/**
  * Same as regular Vaadin's String-Long converter but without numbers formatting which would add "," between digits.
  */
class StringToLongNonFormattingConverter extends StringToLongConverter {

  override protected def getFormat(locale: Locale): NumberFormat = {
    val format = super.getFormat(locale)
    format.setGroupingUsed(false)
    format
  }

}
