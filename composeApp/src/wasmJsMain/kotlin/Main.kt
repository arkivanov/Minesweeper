import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.minesweeper.decodeSerializableContainer
import com.arkivanov.minesweeper.encodeToString
import com.arkivanov.minesweeper.root.DefaultRootComponent
import com.arkivanov.minesweeper.root.RootContent
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val stateKeeper = StateKeeperDispatcher(savedState = localStorage[KEY_SAVED_STATE]?.decodeSerializableContainer())

    val root =
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle, stateKeeper = stateKeeper),
            storeFactory = DefaultStoreFactory(),
        )

    lifecycle.resume()

    window.onbeforeunload =
        {
            localStorage[KEY_SAVED_STATE] = stateKeeper.save().encodeToString()
            null
        }

    // TODO: Take the title from resources after https://youtrack.jetbrains.com/issue/KT-49981
    CanvasBasedWindow(title = "Minesweeper", canvasElementId = "ComposeTarget") {
        RootContent(root)
    }
}

private const val KEY_SAVED_STATE = "saved_state"
