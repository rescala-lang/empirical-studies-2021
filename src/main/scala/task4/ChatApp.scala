package task4

import loci.registry.{Binding, Registry}
import org.scalajs.dom.document
import org.scalajs.dom.html.{Div, Input}
import rescala.default._
import rescala.extra.Tags._
import rescala.extra.distribution.{Network, WebRTCHandling}
import rescala.extra.lattices.Lattice
import rescala.extra.lattices.sequences.RGA
import rescala.extra.lattices.sequences.RGA.RGA
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import task4.Codecs._
import loci.serializer.jsoniterScala.jsoniteScalaBasedSerializable

case class Chatline(author: String, message: String)

case class Epoche[T](number: Int, payload: T)

object ChatApp {
  // convince intellij that the import is needed :-)
  jsoniteScalaBasedSerializable[String]

  val registry = new Registry

  implicit def makeEpocheInstance[T: Lattice]: Lattice[Epoche[T]] = new Lattice[Epoche[T]] {
    override def merge(left: Epoche[T], right: Epoche[T]): Epoche[T] = {
      if (left.number > right.number) left
      else if (left.number < right.number) right
      else Epoche(number = left.number, payload = Lattice[T].merge(left.payload, right.payload))
    }
  }

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
