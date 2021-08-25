package task4

import loci.registry.{Binding, Registry}
import loci.serializer.jsoniterScala.jsoniteScalaBasedSerializable
import org.scalajs.dom.document
import org.scalajs.dom.html.{Div, Input}
import rescala.default._
import rescala.extra.Tags._
import rescala.extra.distribution.{Network, WebRTCHandling}
import rescala.extra.lattices.sequences.{LatticeSequence, RGA}
import rescala.extra.lattices.sequences.RGA.RGA
import rescala.extra.lattices.Lattice
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import task4.Codecs._
import com.github.plokhotnyuk.jsoniter_scala.core._
import org.scalajs.dom

case class Chatline(author: String, message: String, date: Long)

object ChatApp {
  // convince intellij that the import is needed :-)
  jsoniteScalaBasedSerializable[String]

  val registry = new Registry

  def main(args: Array[String]): Unit = {
    val contents = getContents()
    document.body.replaceChild(contents.render, document.body.firstChild)
    document.body.appendChild(p(style := "height: 3em").render)
    document.body.appendChild(WebRTCHandling(registry).webrtcHandlingArea.render)

  }

  def getContents(): TypedTag[Div] = {

    val nameInput = input(placeholder := "Your Name")
    val (nameEvent, renderedName): (Event[String], Input) =
      RenderUtil.inputFieldHandler(nameInput, oninput, clear = false)

    val messageInput = input()
    val (messageEvent, renderedMessage): (Event[String], Input) =
      RenderUtil.inputFieldHandler(messageInput, onchange, clear = true)

    val nameSignal: Signal[String] = nameEvent.latest(renderedName.value)

    /*
    Task 4A:
      This application models a very simple chat application where each user may enter a name, and then send messages which are replicated to all other devices.
      In the current version, the next two definitions create a `Chatline` for each new message a user enters. A chatline is just the message together with the date and author of the message.
      Then, all chatlines are collected into a history.
      The history is an RGA, which is an expensive replicated data structure that ensures correct order.
      However, you note that each chatline has an associated date, and chat messages could just be ordered by that date, thus not requiring the cost of the RGA.

      Replace the `RGA[Chatline]` in the history with a simple `List[Chatline]`. You may assume that the creation date of new messages on the current device is always ascending. You may also assume that clocks between different devices are reasonably synchronized.

      To enable replication of your new list of chatlines, the system requires an instance of `Lattice[List[Chatline]]` (instead of the predefined `RGA.lattice`. Implement that instance ensuring that the merge method is associative, commutative, and idempotent.

     */

    /*
    Task4B:
      Its annoying, that the chat history is lost every time the browser window is restarted!
      Fix that by storing and loading the history from local storage.

      You may use the following constructs for reading and writing objects of type `A` to local storage:

     `readFromString[A](dom.window.localStorage.getItem(key))`
     `dom.window.localStorage.setItem(key, writeToString(ft))`

     */

    val chatline: Event[Chatline] = messageEvent.map { msg =>
      Chatline(nameSignal.value, msg, System.currentTimeMillis())
    }

    val history: Signal[RGA[Chatline]] =
      chatline.fold(RGA.empty[Chatline])((current, line) => current.prepend(line))

    Network.replicate(history, registry)(Binding("history"))(RGA.lattice)


    // End of code relevant for the task

    val chatDisplay = Signal.dynamic {
      val reversedHistory = history.value.toList.reverse
      reversedHistory.map { case Chatline(author, message, date) =>
        val styleString = if (chatline.value.contains(Chatline(author, message, date))) "color: red" else ""
        p(s"${author}: $message", style := styleString)
      }
    }

    div(div(chatDisplay.asModifierL), renderedName, renderedMessage)
  }

}
