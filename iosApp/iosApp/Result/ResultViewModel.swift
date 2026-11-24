import Foundation
import SharedLogic

/// Optional: only needed if you navigate to Result independently (by code)
/// instead of passing an Examinee in from ScanView. With the current flow
/// (ScanView holds the HomeStore and passes the result), this isn't required.
@MainActor
final class ResultViewModel: ObservableObject {
    @Published var examinee: Examinee?
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let store: HomeStore

    init(container: IosContainer) {
        self.store = container.makeHomeStore()
        observe()
    }

    private func observe() {
        Task { [weak self] in
            guard let self else { return }
            for await s in store.state {
                self.examinee = s.examinee
                self.isLoading = s.isLoading
                self.errorMessage = s.errorMessage
            }
        }
    }

    func lookup(_ code: String) { store.onScan(scannedValue: code) }
    func clear()                { store.clearResult() }
}