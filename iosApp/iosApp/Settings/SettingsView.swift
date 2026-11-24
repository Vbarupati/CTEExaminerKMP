import SwiftUI

struct SettingsView: View {
    @StateObject var vm: SettingsViewModel
    var onBack: () -> Void

    @AppStorage("darkMode") private var darkMode = false
    @State private var showEditUrl = false
    @State private var draftUrl = ""

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Button {
                        draftUrl = vm.baseUrl
                        showEditUrl = true
                    } label: {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Change Base Url")
                                .foregroundColor(.primary)
                            Text(vm.baseUrl.isEmpty ? "Not set" : vm.baseUrl)
                                .font(.footnote)
                                .foregroundColor(.secondary)
                        }
                    }
                }

                Section {
                    Toggle("Dark Theme", isOn: $darkMode)
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onBack) {
                        Image(systemName: "arrow.left")
                    }
                }
            }
            // Change Base URL dialog
            .alert("Change Base Url", isPresented: $showEditUrl) {
                TextField("Base URL", text: $draftUrl)
                    .textInputAutocapitalization(.never)
                    .disableAutocorrection(true)
                Button("Cancel", role: .cancel) {}
                Button("OK") { vm.changeBaseUrl(draftUrl) }
            }
        }
    }
}