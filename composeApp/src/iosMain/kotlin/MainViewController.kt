import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.minesweeper.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    ComposeUIViewController { App() }
