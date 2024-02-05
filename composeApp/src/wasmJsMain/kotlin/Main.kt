import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.minesweeper.root.DefaultRootComponent
import com.arkivanov.minesweeper.root.RootContent
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    val root =
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            storeFactory = DefaultStoreFactory(),
        )

    lifecycle.resume()

    CanvasBasedWindow(title = "Minesweeper", canvasElementId = "ComposeTarget") {
        RootContent(root)
    }
}
