import SwiftUI

struct StatusPill: View {
    let verified: Bool

    var body: some View {
        Text(verified ? "● Authentic & Active" : "● Not Verified")
            .font(.caption2).bold()
            .foregroundColor(verified ? Theme.green : Theme.red)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background((verified ? Theme.green : Theme.red).opacity(0.12))
            .clipShape(Capsule())
    }
}