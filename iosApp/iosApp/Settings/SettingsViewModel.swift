import Foundation
import SharedLogic

@MainActor
final class SettingsViewModel: ObservableObject {
    @Published var baseUrl: String = ""

    private let container: IosContainer

    init(container: IosContainer) {
        self.container = container
        observe()
    }

    private func observe() {
        Task { [weak self] in
            guard let self else { return }
            for await url in container.getBaseUrl() {   // shared getBaseUrl(): Flow<String>
                self.baseUrl = url
            }
        }
    }

    func changeBaseUrl(_ url: String) {
        let trimmed = url.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return }
        Task { try? await container.setBaseUrl(url: trimmed) }   // shared setBaseUrl(url:)
    }
}