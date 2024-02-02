import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.minesweeper.App

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "Minesweeper") {
            App()
        }
    }
}
