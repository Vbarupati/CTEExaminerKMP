import Foundation
import SharedLogic

@MainActor
final class ScanViewModel: ObservableObject {
    @Published var state: HomeState

    private let store: HomeStore

    init(container: IosContainer) {
        self.store = container.makeHomeStore()
        self.state = store.state.value
        observe()
        store.loadUserInfo()          // load examiner profile
    }

    private func observe() {
        Task { [weak self] in
            guard let self else { return }
            for await s in store.state {
                self.state = s
            }
        }
    }

    var examinerName: String { state.userInfo?.fullName ?? "" }

    func onScan(_ code: String) { store.onScan(scannedValue: code) }
    func clearResult()          { store.clearResult() }
    func logout()               { store.logout() }
}