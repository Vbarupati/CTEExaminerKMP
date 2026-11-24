import SwiftUI

struct LoginView: View {
    @StateObject var vm: LoginViewModel
    var onLoggedIn: () -> Void
    var onOpenSettings: () -> Void

    private let navy = Color(red: 0x1B/255, green: 0x3C/255, blue: 0x8C/255)
    private let shieldBg = Color(red: 0xE3/255, green: 0xEC/255, blue: 0xFB/255)

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Color(.systemGroupedBackground).ignoresSafeArea()

            // Settings gear (top-right)
            Button(action: onOpenSettings) {
                Image(systemName: "gearshape.fill")
                    .foregroundColor(.secondary)
                    .padding()
            }

            VStack(spacing: 0) {
                // Shield badge
                RoundedRectangle(cornerRadius: 24)
                    .fill(shieldBg)
                    .frame(width: 96, height: 96)
                    .overlay(
                        Image(systemName: "checkmark.shield.fill")
                            .resizable().scaledToFit()
                            .frame(width: 50, height: 50)
                            .foregroundColor(navy)
                    )

                Text("CTE Examiner")
                    .font(.largeTitle).bold()
                    .padding(.top, 20)
                Text("Sign in to verify candidates")
                    .foregroundColor(.secondary)
                    .padding(.top, 4)

                VStack(spacing: 16) {
                    // Username
                    HStack {
                        Image(systemName: "person.fill").foregroundColor(.secondary)
                        TextField("Username / Examiner ID",
                                  text: Binding(get: { vm.state.username },
                                                set: { vm.onUsername($0) }))
                            .textInputAutocapitalization(.never)
                            .disableAutocorrection(true)
                    }
                    .padding()
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(.systemGray4)))

                    // Password
                    HStack {
                        Image(systemName: "lock.fill").foregroundColor(.secondary)
                        Group {
                            if vm.state.isPasswordVisible {
                                TextField("Password",
                                          text: Binding(get: { vm.state.password },
                                                        set: { vm.onPassword($0) }))
                            } else {
                                SecureField("Password",
                                            text: Binding(get: { vm.state.password },
                                                          set: { vm.onPassword($0) }))
                            }
                        }
                        Button(action: vm.togglePassword) {
                            Image(systemName: vm.state.isPasswordVisible ? "eye.fill" : "eye.slash.fill")
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding()
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(.systemGray4)))

                    // Error
                    if let error = vm.state.errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.footnote)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Login button
                    Button(action: vm.login) {
                        Group {
                            if vm.state.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                Text("Login").bold().foregroundColor(.white)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 54)
                    }
                    .background(navy)
                    .cornerRadius(12)
                    .disabled(!vm.state.canSubmit)
                    .opacity(vm.state.canSubmit ? 1 : 0.5)
                    .padding(.top, 12)
                }
                .padding(.top, 40)
                .padding(.horizontal, 32)
            }
            .padding(.horizontal)
        }
        .onChange(of: vm.state.isLoggedIn) { loggedIn in
            if loggedIn { onLoggedIn() }
        }
    }
}