import Foundation
import SharedLogic

@MainActor
final class ResultViewModel: ObservableObject {
    @Published var examinee: Examinee?
    @Published var isLoading = false
    @Published var errorMessage: String?
    private let store: HomeStoreIos
    init(container: IosContainer) {
        let store = container.makeHomeStore()
        self.store = store
        store.observe { [weak self] s in
            self?.examinee = s.examinee
            self?.isLoading = s.isLoading
            self?.errorMessage = s.errorMessage
        }
    }
    func lookup(_ code: String) { store.onScan(scannedValue: code) }
    func clear() { store.clearResult() }
}
