import Foundation
import SharedLogic

@MainActor
final class LoginViewModel: ObservableObject {
    @Published var state: LoginState
    private let store: LoginStoreIos
    init(container: IosContainer) {
        let store = container.makeLoginStore()
        self.store = store
        self.state = store.current
        store.observe { [weak self] s in self?.state = s }
    }
    func onUsername(_ v: String) { store.onUsernameChange(value: v) }
    func onPassword(_ v: String) { store.onPasswordChange(value: v) }
    func togglePassword()        { store.togglePasswordVisibility() }
    func login()                 { store.login() }
}