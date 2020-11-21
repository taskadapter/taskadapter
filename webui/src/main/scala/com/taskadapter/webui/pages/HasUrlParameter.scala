package com.taskadapter.webui.pages

trait HasUrlParameter {
  def setParameter(event: BeforeEvent, configIdStr: String)

}

class BeforeEvent() {

}