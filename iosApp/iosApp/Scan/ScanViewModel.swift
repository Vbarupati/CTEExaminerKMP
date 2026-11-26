import Foundation
import SharedLogic

@MainActor
final class ScanViewModel: ObservableObject {
    @Published var state: HomeState
    private let store: HomeStoreIos
    init(container: IosContainer) {
        let store = container.makeHomeStore()
        self.store = store
        self.state = store.current
        store.observe { [weak self] s in self?.state = s }
        store.loadUserInfo()
    }
    var examinerName: String { state.userInfo?.fullName ?? "" }
    func onScan(_ code: String) { store.onScan(scannedValue: code) }
    func clearResult()          { store.clearResult() }
    func logout()               { store.logout() }
}