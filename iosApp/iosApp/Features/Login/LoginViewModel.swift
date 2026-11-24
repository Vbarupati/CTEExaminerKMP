import Foundation
import SharedLogic

@MainActor
final class LoginViewModel: ObservableObject {
    @Published var state: LoginState

    private let store: LoginStore

    init(container: IosContainer) {
        self.store = container.makeLoginStore()
        self.state = store.state.value          // SKIE exposes StateFlow.value
        observe()
    }

    private func observe() {
        Task { [weak self] in
            guard let self else { return }
            for await s in store.state {         // SKIE: StateFlow is an AsyncSequence
                self.state = s
            }
        }
    }

    func onUsername(_ v: String) { store.onUsernameChange(value: v) }
    func onPassword(_ v: String) { store.onPasswordChange(value: v) }
    func togglePassword()        { store.togglePasswordVisibility() }
    func login()                 { store.login() }
}