import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.minesweeper.App
import com.arkivanov.minesweeper.game.DefaultGameComponent
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root =
        DefaultGameComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            storeFactory = DefaultStoreFactory(),
        )

    lifecycle.resume()

    CanvasBasedWindow(canvasElementId = "ComposeTarget") { App(root ) }
}
