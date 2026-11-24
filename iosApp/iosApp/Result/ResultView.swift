import SwiftUI
import SharedLogic

struct ResultView: View {
    let examinee: Examinee
    var onScanNext: () -> Void

    // Brand colors (match Android)
    private let navy = Color(red: 0x17/255, green: 0x2D/255, blue: 0x70/255)
    private let orange = Color(red: 0xFF/255, green: 0x98/255, blue: 0x00/255)
    private let green = Color(red: 0x2E/255, green: 0x7D/255, blue: 0x32/255)
    private let labelGrey = Color(red: 0x4A/255, green: 0x5A/255, blue: 0x6A/255)
    private let pageBg = Color(red: 0xF5/255, green: 0xF7/255, blue: 0xFB/255)

    private var verified: Bool { examinee.isAllowed }
    private var notFound: Bool { !examinee.found }

    var body: some View {
        VStack(spacing: 0) {
            // Top bar
            HStack {
                Button(action: onScanNext) {
                    Image(systemName: "arrow.left").foregroundColor(navy)
                }
                Text("Scan Result").font(.title3).bold().foregroundColor(navy)
                Spacer()
            }
            .padding()
            .background(Color.white)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {

                    // Brand + status pill
                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("trakaid").font(.title2).bold().foregroundColor(navy)
                            Text("Digital Trail of Your Assets")
                                .font(.caption2).foregroundColor(.secondary)
                        }
                        Spacer()
                        statusPill
                    }

                    if verified { verifiedCard } else { notVerifiedCard }

                    if notFound {
                        notFoundBlock
                    } else {
                        certificateCard
                        detailsCard
                        deliveryCard
                    }
                }
                .padding(16)
            }
            .background(pageBg)

            // Scan Next
            Button(action: onScanNext) {
                HStack {
                    Image(systemName: "qrcode.viewfinder")
                    Text("Scan Next").bold()
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity).frame(height: 54)
            }
            .background(navy)
            .cornerRadius(14)
            .padding(16)
            .background(Color.white)
        }
    }

    // MARK: pieces

    private var statusPill: some View {
        Text(verified ? "● Authentic & Active" : "● Not Verified")
            .font(.caption2).bold()
            .foregroundColor(verified ? green : .red)
            .padding(.horizontal, 12).padding(.vertical, 8)
            .background((verified ? green : Color.red).opacity(0.12))
            .clipShape(Capsule())
    }

    private var verifiedCard: some View {
        card {
            Text("● CERTIFICATE VERIFIED").font(.caption).bold()
                .foregroundColor(orange)
            (Text("This asset is ").foregroundColor(navy)
             + Text("genuine.").foregroundColor(orange))
                .font(.system(size: 32, weight: .heavy))
            Text("This page confirms the asset below has been verified through the Trakaid Digital Trust Network.")
                .font(.subheadline).foregroundColor(labelGrey)
            HStack {
                tag("Non-fakeable identity")
                tag("Batch-linked origin")
            }
        }
    }

    private var notVerifiedCard: some View {
        card {
            Text("● NOT VERIFIED").font(.caption).bold().foregroundColor(.red)
            Text("This asset is not verified by the Trakaid Digital Trust Network.")
                .font(.title3).bold().foregroundColor(navy)
        }
    }

    private var notFoundBlock: some View {
        VStack(spacing: 12) {
            ZStack {
                Circle().fill(Color.red.opacity(0.1)).frame(width: 90, height: 90)
                Image(systemName: "exclamationmark.circle")
                    .resizable().scaledToFit().frame(width: 44, height: 44)
                    .foregroundColor(.red)
            }
            Text("Asset Not Found").font(.title3).bold().foregroundColor(navy)
            Text("No asset is linked to this code. Please check the code and scan again.")
                .font(.subheadline).foregroundColor(labelGrey)
                .multilineTextAlignment(.center)
            VStack(spacing: 4) {
                Text("ASSET QUALITY CODE").font(.caption).foregroundColor(.secondary)
                Text(examinee.assetQualityCode).font(.title3).bold().foregroundColor(navy)
            }
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color.white)
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color(.systemGray5)))
        }
        .frame(maxWidth: .infinity)
        .padding(.top, 8)
    }

    private var certificateCard: some View {
        card {
            Text("CERTIFICATE OF AUTHENTICITY").font(.caption).bold().foregroundColor(orange)
            Text("Verified by the Trakaid Digital Trust Network")
                .font(.title2).bold().foregroundColor(navy)
            VStack(alignment: .leading, spacing: 8) {
                Text("ASSET NUMBER").font(.caption).foregroundColor(.secondary)
                Text(examinee.ownerANum ?? "—").font(.largeTitle).bold().foregroundColor(navy)
            }
            .padding().frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 0xF5/255, green: 0xF8/255, blue: 0xFC/255))
            .cornerRadius(16)
        }
    }

    private var detailsCard: some View {
        card {
            Text("ASSET INFORMATION").font(.caption).bold().foregroundColor(orange)
            row("Owner name", examinee.ownerName)
            row("Product", examinee.product)
            row("Filled at", examinee.filledAt)
            row("Batch number", examinee.batchNumber)
            row("Batch date", examinee.batchDate)
            row("Expiry date", examinee.expiryDate)
            row("Asset quality code", examinee.assetQualityCode)
        }
    }

    private var deliveryCard: some View {
        card {
            Text("DELIVERY STATUS").font(.caption).bold().foregroundColor(labelGrey)
            Text(examinee.status.isEmpty ? "Status not available" : examinee.status)
                .font(.title3).bold().foregroundColor(navy)
        }
    }

    // MARK: helpers

    @ViewBuilder
    private func row(_ label: String, _ value: String?) -> some View {
        if let v = value, !v.isEmpty {
            HStack(alignment: .top) {
                Text(label).font(.caption).foregroundColor(.secondary)
                    .frame(width: 120, alignment: .leading)
                Text(v).font(.subheadline).bold()
                Spacer()
            }
        }
    }

    private func tag(_ text: String) -> some View {
        Text(text).font(.caption2).bold().foregroundColor(navy)
            .padding(.horizontal, 10).padding(.vertical, 8)
            .background(Color(red: 0xEB/255, green: 0xF0/255, blue: 0xFF/255))
            .clipShape(Capsule())
    }

    private func card<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12, content: content)
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.white)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color(.systemGray5)))
            .cornerRadius(20)
    }
}