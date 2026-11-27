import SwiftUI

struct ScanView: View {
    @StateObject var vm: ScanViewModel
    var onLoggedOut: () -> Void
    var onOpenSettings: () -> Void

    @State private var showManual = false
    @State private var confirmLogout = false
    @State private var errorText: String?

    var body: some View {
        NavigationStack {
            ZStack {
                QRScannerView { code in vm.onScan(code) }
                    .ignoresSafeArea()

                ScannerOverlay()

                VStack {
                    Spacer()
                    Text("Align the QR code inside the frame")
                        .foregroundColor(.white)
                        .padding(.bottom, 40)
                    Button {
                        showManual = true
                    } label: {
                        Label("Enter code", systemImage: "keyboard")
                            .padding(.horizontal, 8)
                    }
                    .buttonStyle(.borderedProminent)
                    .padding(.bottom, 24)
                }

                if vm.state.isLoading {
                    Color.black.opacity(0.4).ignoresSafeArea()
                    ProgressView().tint(.white).scaleEffect(1.4)
                }
            }
            .navigationTitle(vm.examinerName.isEmpty ? "Scan QR" : "Examiner: \(vm.examinerName)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onOpenSettings) { Image(systemName: "gearshape") }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button { confirmLogout = true } label: {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                    }
                }
            }
            // Full-screen certificate when a result arrives
            .fullScreenCover(isPresented: Binding(
                get: { vm.state.examinee != nil },
                set: { if !$0 { vm.clearResult() } }
            )) {
                if let examinee = vm.state.examinee {
                    ResultView(examinee: examinee, onScanNext: { vm.clearResult() })
                }
            }
            .alert("Logout?", isPresented: $confirmLogout) {
                Button("Cancel", role: .cancel) {}
                Button("Logout", role: .destructive) { vm.logout() }
            } message: {
                Text("You'll need to sign in again to scan.")
            }
            .sheet(isPresented: $showManual) {
                ManualCodeSheet { code in
                    showManual = false
                    vm.onScan(code)
                }
            }
            // Surface lookup errors (expired session, code not found, network)
            .alert("Couldn't find asset", isPresented: Binding(
                get: { errorText != nil },
                set: { if !$0 { errorText = nil } }
            )) {
                Button("OK", role: .cancel) {}
            } message: {
                Text(errorText ?? "")
            }
            .onChange(of: vm.state.errorMessage) { msg in
                if let msg, !msg.isEmpty { errorText = msg }
            }
            .onChange(of: vm.state.loggedOut) { out in
                if out { onLoggedOut() }
            }
        }
    }
}

// MARK: - Scan frame overlay

struct ScannerOverlay: View {
    var body: some View {
        GeometryReader { geo in
            let box = min(geo.size.width, geo.size.height) * 0.68
            ZStack {
                Color.black.opacity(0.5)
                    .mask(
                        Rectangle()
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .frame(width: box, height: box)
                                    .blendMode(.destinationOut)
                            )
                    )
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.white, lineWidth: 3)
                    .frame(width: box, height: box)
            }
            .compositingGroup()
            .ignoresSafeArea()
        }
    }
}

// MARK: - Manual code entry

struct ManualCodeSheet: View {
    var onSubmit: (String) -> Void
    @Environment(\.dismiss) private var dismiss
    @State private var code = ""

    var body: some View {
        NavigationStack {
            Form {
                TextField("Roll no. / QR code", text: $code)
                    .autocorrectionDisabled(true)
                    .textInputAutocapitalization(.characters)
                    .submitLabel(.search)
                    .onSubmit(submit)
            }
            .navigationTitle("Enter code")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Search", action: submit)
                        .disabled(code.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
        }
    }

    private func submit() {
        let clean = code.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        guard !clean.isEmpty else { return }
        onSubmit(clean)
    }
}
