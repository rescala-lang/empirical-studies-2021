package task5

import loci.registry.Registry
import org.scalajs.dom.document
import rescala.extra.distribution.WebRTCHandling

import java.util.concurrent.ThreadLocalRandom

/*
 Task5:

 This todolist application is a full implementation of a common case study for interactive applications.
 Your task is to document the dataflow in this application by creating a diagram (either in text or as an image) of how interactions (such as user adding new tasks) travel through the program.
 Target the level of detail of your description to someone else who wants to understand this particular codebase.

 */

object Todolist {

  val replicaId: String = ThreadLocalRandom.current().nextLong().toHexString

  val todoApp = new TodoAppUI()

  val registry = new Registry

  def main(args: Array[String]): Unit = {

    val div = todoApp.getContents()

    val webrtc = WebRTCHandling(registry)

    document.body.replaceChild(div.render, document.body.firstElementChild)
    document.body.appendChild(webrtc.webrtcHandlingArea.render)

  }

}
