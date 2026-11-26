import Foundation
import SharedLogic

@MainActor
final class SettingsViewModel: ObservableObject {
    @Published var baseUrl: String = ""
    private let settings: SettingsIos
    init(container: IosContainer) {
        let s = container.makeSettings()
        self.settings = s
        s.observeBaseUrl { [weak self] url in self?.baseUrl = url }
    }
    func changeBaseUrl(_ url: String) {
        let trimmed = url.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return }
        settings.setBaseUrl(url: trimmed)
    }
}