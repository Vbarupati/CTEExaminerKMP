import SwiftUI

struct InfoCard: View {
    let label: String
    let value: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(label.uppercased())
                .font(.caption).bold()
                .foregroundColor(Theme.labelGrey)
            Text((value?.isEmpty == false ? value! : "—"))
                .font(.headline)
                .foregroundColor(Color(red: 0x14/255, green: 0x21/255, blue: 0x3D/255))
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Theme.cardBorder))
        .cornerRadius(16)
    }
}