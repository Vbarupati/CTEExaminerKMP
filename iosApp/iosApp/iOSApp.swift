import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    @StateObject private var app = AppState()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(app)
        }
    }
}

// MARK: - Navigation targets

enum Screen {
    case login
    case scan
    case settings
}

// MARK: - App-wide state (owns the shared DI container)

@MainActor
final class AppState: ObservableObject {
    let container = IosContainer()          // builds the shared graph (iOS storage + Ktor)

    @Published var screen: Screen = .login
    @Published var cameFromScan = false     // so Settings knows where "back" goes

    func goToScan()       { screen = .scan }
    func goToLogin()      { screen = .login }
    func openSettings(fromScan: Bool) {
        cameFromScan = fromScan
        screen = .settings
    }
    func closeSettings()  { screen = cameFromScan ? .scan : .login }
}

// MARK: - Root switch

struct RootView: View {
    @EnvironmentObject var app: AppState

    var body: some View {
        switch app.screen {

        case .login:
            LoginView(
                vm: LoginViewModel(container: app.container),
                onLoggedIn: { app.goToScan() },
                onOpenSettings: { app.openSettings(fromScan: false) }
            )

        case .scan:
            ScanView(
                vm: ScanViewModel(container: app.container),
                onLoggedOut: { app.goToLogin() },
                onOpenSettings: { app.openSettings(fromScan: true) }
            )

        case .settings:
            SettingsView(
                vm: SettingsViewModel(container: app.container),
                onBack: { app.closeSettings() }
            )
        }
    }
}