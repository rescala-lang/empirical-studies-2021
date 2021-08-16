package task4

import loci.registry.{Binding, Registry}
import loci.serializer.jsoniterScala.jsoniteScalaBasedSerializable
import org.scalajs.dom.document
import org.scalajs.dom.html.{Div, Input}
import rescala.default._
import rescala.extra.Tags._
import rescala.extra.distribution.{Network, WebRTCHandling}
import rescala.extra.lattices.sequences.RGA
import rescala.extra.lattices.sequences.RGA.RGA
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import task4.Codecs._

case class Chatline(author: String, message: String)


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

    val nameSignal: Signal[String] = Storing.storedAs("name", "") { init =>
      renderedName.value = init
      nameEvent.latest(init)
    }

    val chatline: Event[Chatline] = messageEvent.map { msg => Chatline(nameSignal.value, msg) }

    val deleteAll = Evt[Unit]()

    val history: Signal[Epoche[RGA[Chatline]]] =
      Storing.storedAs("history", Epoche(0, RGA.empty[Chatline])) { initial =>
        Events.foldAll(initial) { current =>
          Seq(
            chatline act { line => current.copy(payload = current.payload.prepend(line)) },
            deleteAll act { arg => Epoche(number = current.number + 1, RGA.empty[Chatline]) }
          )
        }
      }

    Network.replicate(history, registry)(Binding("history"))

    val chatDisplay = Signal.dynamic {
      val reversedHistory = history.value.payload.toList.reverse
      reversedHistory.map { case Chatline(author, message) =>
        val styleString = if (chatline.value.contains(Chatline(author, message))) "color: red" else ""
        p(s"${author}: $message", style := styleString)
      }
    }

    div(div(chatDisplay.asModifierL), renderedName, renderedMessage)
  }

}
