import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.minesweeper.root.DefaultRootComponent
import com.arkivanov.minesweeper.root.RootContent
import com.arkivanov.mvikotlin.timetravel.server.TimeTravelServer
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import javax.swing.SwingUtilities

fun main() {
    TimeTravelServer(runOnMainThread = { SwingUtilities.invokeLater(it) })
        .start()

    val lifecycle = LifecycleRegistry()

    val root =
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            storeFactory = TimeTravelStoreFactory(),
        )

    application {
        val windowState = rememberWindowState()

        Window(onCloseRequest = ::exitApplication, title = "Minesweeper", state = windowState) {
            RootContent(component = root)
        }

        @OptIn(ExperimentalDecomposeApi::class)
        LifecycleController(lifecycle, windowState)
    }
}
