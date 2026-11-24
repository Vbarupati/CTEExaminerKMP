import SwiftUI

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