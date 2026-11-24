import SwiftUI
import SharedLogic

enum Screen {
    case login
    case scan
    case settings
}

@MainActor
final class AppState: ObservableObject {
    let container = IosContainer()          // shared DI graph (iOS storage + Ktor)

    @Published var screen: Screen = .login
    @Published var cameFromScan = false     // so Settings knows where "Back" returns

    func goToScan()  { screen = .scan }
    func goToLogin() { screen = .login }

    func openSettings(fromScan: Bool) {
        cameFromScan = fromScan
        screen = .settings
    }

    func closeSettings() {
        screen = cameFromScan ? .scan : .login
    }
}