import SwiftUI
import SharedLogic

private extension Color {
    static let labelGrey      = Color(hex: 0x4A5A6A)
    static let cardBorder     = Color(hex: 0xE2E6EC)
    static let brandBlue      = Color(hex: 0x172D70)
    static let brandOrange    = Color(hex: 0xFF9800)
    static let pendingAmber   = Color(hex: 0xB26A00)
    static let pageBackground = Color(hex: 0xF5F7FB)
    static let errorRed       = Color(hex: 0xD32F2F)
    static let successGreen   = Color(hex: 0x1E8E3E)
    static let infoValue      = Color(hex: 0x14213D)
    init(hex: UInt32) {
        self.init(red: Double((hex >> 16) & 0xFF)/255.0,
                  green: Double((hex >> 8) & 0xFF)/255.0,
                  blue: Double(hex & 0xFF)/255.0)
    }
}

struct ResultView: View {
    @StateObject var vm: ResultViewModel
    let code: String
    let onScanNext: () -> Void

    var body: some View {
        VStack(spacing: 0) { topBar; content }
            .background(Color.pageBackground.ignoresSafeArea())
            .safeAreaInset(edge: .bottom) { scanNextBar }
            .onAppear { vm.lookup(code) }
    }

    private var topBar: some View {
        HStack {
            Button(action: onScanNext) { Image(systemName: "arrow.left").foregroundColor(.brandBlue) }
            Spacer()
            Text("Scan Result").fontWeight(.bold).foregroundColor(.brandBlue)
            Spacer()
            Image(systemName: "arrow.left").opacity(0)
        }
        .padding(.horizontal, 16).padding(.vertical, 12).background(Color.white)
    }

    private var scanNextBar: some View {
        Button(action: onScanNext) {
            HStack {
                Image(systemName: "qrcode.viewfinder")
                Text("Scan Next").fontWeight(.bold).font(.system(size: 16))
            }
            .frame(maxWidth: .infinity).frame(height: 54)
            .foregroundColor(.white).background(Color.brandBlue)
            .clipShape(RoundedRectangle(cornerRadius: 14))
        }
        .padding(16).background(Color.white.shadow(radius: 4))
    }

    @ViewBuilder private var content: some View {
        if let message = vm.errorMessage {
            ScrollView { ErrorContent(message: message, actionLabel: "Scan again", onAction: onScanNext) }
        } else if let info = vm.examinee {
            if isAssetNotFound(info) { ScrollView { AssetNotFoundContent(code: info.assetQualityCode) } }
            else { ScrollView { ResultContent(info: info) } }
        } else {
            VStack(spacing: 12) { ProgressView().tint(.brandOrange); Text("Fetching details…") }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private func isAssetNotFound(_ i: Examinee) -> Bool {
        (i.ownerName?.isEmpty ?? true) && (i.product?.isEmpty ?? true) &&
        (i.batchNumber?.isEmpty ?? true) && (i.filledAt?.isEmpty ?? true)
    }
}

private struct ResultContent: View {
    let info: Examinee
    private var verified: Bool { info.isAllowed }
    private var assetNo: String { if let a = info.ownerANum, !a.isEmpty { return a }; return "—" }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack { BrandLogo(); Spacer(); VerificationPill(verified: verified) }

            ResultWhiteCard {
                CapsLabel(verified ? "● CERTIFICATE VERIFIED" : "● NOT VERIFIED",
                          color: verified ? .brandOrange : .errorRed)
                (Text("This asset is ").foregroundColor(.brandBlue)
                 + Text(verified ? "genuine." : "not verified.").foregroundColor(verified ? .brandOrange : .errorRed))
                    .font(.system(size: 34, weight: .heavy))
                Text("This page confirms that the asset below has been verified through Trakaid Assetotrak. The certificate links the asset identity, product details, batch information and delivery status into one digital trust record.")
                    .font(.system(size: 14)).foregroundColor(.labelGrey)
                HStack(spacing: 8) { TrustTag("Non-fakeable identity"); TrustTag("Batch-linked origin") }
            }

            ResultWhiteCard {
                CapsLabel("CERTIFICATE OF AUTHENTICITY", color: .brandOrange)
                Text("Verified by the Trakaid Digital Trust Network")
                    .font(.system(size: 27, weight: .heavy)).foregroundColor(.brandBlue)
                RoundedRectangle(cornerRadius: 16).fill(Color(hex: 0xF5F8FC))
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.cardBorder))
                    .overlay(VStack(alignment: .leading, spacing: 8) {
                        CardLabel("ASSET NUMBER")
                        Text(assetNo).font(.system(size: 32, weight: .heavy)).foregroundColor(.brandBlue)
                    }.padding(16), alignment: .leading)
                    .frame(maxWidth: .infinity, minHeight: 92)
                HStack {
                    ZStack {
                        Circle().fill(verified ? Color.brandOrange : Color.errorRed).frame(width: 68, height: 68)
                        Text(verified ? "GENUINE\nCERTIFIED" : "NOT\nVERIFIED")
                            .font(.system(size: 10, weight: .heavy)).multilineTextAlignment(.center).foregroundColor(.white)
                    }
                    Text(verified ? "Certificate data has been matched against the product information."
                                  : "This asset could not be matched against the product records. Please treat it with caution.")
                        .font(.system(size: 12)).foregroundColor(verified ? .labelGrey : .errorRed)
                }
            }

            CapsLabel("ASSET INFORMATION", color: .brandOrange).font(.system(size: 13, weight: .heavy))
            InfoCardRow("Owner name", info.ownerName, "Product", info.product)
            InfoCardRow("Filled at", info.filledAt, "Batch number", info.batchNumber)
            InfoCardRow("Batch date", info.batchDate, "Expiry date", info.expiryDate)
            ResultInfoCard("Asset quality code", info.assetQualityCode).frame(maxWidth: .infinity)

            DeliveryStatusCard(status: info.status)

            ResultWhiteCard {
                CapsLabel("DIGITAL TRUST PROOF", color: .brandOrange)
                Text("What this certificate confirms").font(.system(size: 20, weight: .heavy)).foregroundColor(.brandBlue)
                CheckLine("The asset number is linked to a specific product and owner record.")
                CheckLine("The batch details and fill location are visible to the customer.")
                CheckLine("The delivery status is presented as part of the certificate record.")
                CheckLine("The certificate creates a customer-facing proof point for authenticity and trust.")
            }

            ResultWhiteCard {
                CapsLabel("DIGITAL TRAIL", color: .brandOrange)
                Text("Asset verification flow").font(.system(size: 20, weight: .heavy)).foregroundColor(.brandBlue)
                StepRow(1, "Asset identified", "Asset number \(assetNo) is attached to the physical item.", last: false)
                StepRow(2, "Batch recorded", "Batch \(info.batchNumber ?? "—") is linked to the product record.", last: false)
                StepRow(3, "Certificate generated", "The customer can scan the QR code to view the certificate page.", last: false)
                StepRow(4, "Delivery status", (info.status.isEmpty ? "Status not available" : info.status), last: true)
            }

            Text("Trakaid Assetotrak · Certificate of Authenticity")
                .font(.system(size: 12, weight: .bold)).foregroundColor(.brandBlue)
            Text("Protected digital trust record · support@trakaid.com")
                .font(.system(size: 12)).foregroundColor(.labelGrey)
        }
        .padding(16)
    }
}

private struct AssetNotFoundContent: View {
    let code: String?
    var body: some View {
        VStack(spacing: 14) {
            HStack { BrandLogo(); Spacer(); VerificationPill(verified: false) }
            ResultWhiteCard {
                CapsLabel("● NOT VERIFIED", color: .errorRed)
                (Text("This asset is ").foregroundColor(.brandBlue)
                 + Text("not verified").foregroundColor(.errorRed)
                 + Text(" by the Trakaid Digital Trust Network.").foregroundColor(.brandBlue))
                    .font(.system(size: 22, weight: .heavy))
            }
            VStack(spacing: 8) {
                ZStack {
                    RoundedRectangle(cornerRadius: 50).fill(Color.errorRed.opacity(0.10)).frame(width: 80, height: 80)
                    Image(systemName: "exclamationmark.circle").font(.system(size: 42)).foregroundColor(.errorRed)
                }
                Text("Asset Not Found").font(.system(size: 20, weight: .heavy)).foregroundColor(.brandBlue)
                Text("No asset is linked to this code. Please check the code and scan again.")
                    .font(.system(size: 13)).foregroundColor(.labelGrey).multilineTextAlignment(.center)
                if let code, !code.isEmpty {
                    RoundedRectangle(cornerRadius: 12).fill(Color.white)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.cardBorder))
                        .overlay(VStack(spacing: 4) {
                            CardLabel("ASSET QUALITY CODE")
                            Text(code).font(.system(size: 16, weight: .heavy)).foregroundColor(.brandBlue)
                        }.padding(.horizontal, 24).padding(.vertical, 12))
                        .fixedSize()
                }
            }.padding(.top, 8)
        }
        .padding(16)
    }
}

private struct BrandLogo: View {
    var body: some View {
        if UIImage(named: "trakaid_logo") != nil {
            Image("trakaid_logo").resizable().scaledToFit().frame(height: 56)
        } else {
            VStack(alignment: .leading, spacing: 2) {
                Text("trakaid").font(.system(size: 24, weight: .heavy)).foregroundColor(.brandBlue)
                Text("Digital Trail of Your Assets").font(.system(size: 10)).foregroundColor(.labelGrey)
            }
        }
    }
}

private struct VerificationPill: View {
    let verified: Bool
    var body: some View {
        Text(verified ? "● Authentic & Active" : "● Not Verified")
            .font(.system(size: 10, weight: .bold))
            .foregroundColor(verified ? .successGreen : .errorRed)
            .padding(.horizontal, 10).padding(.vertical, 9)
            .background(verified ? Color(hex: 0xE8F7EE) : Color.errorRed.opacity(0.10))
            .clipShape(RoundedRectangle(cornerRadius: 30))
            .overlay(RoundedRectangle(cornerRadius: 30).stroke(verified ? Color(hex: 0xB8E3C8) : Color.errorRed.opacity(0.4)))
    }
}

private struct ResultWhiteCard<Content: View>: View {
    @ViewBuilder let content: Content
    var body: some View {
        VStack(alignment: .leading, spacing: 12) { content }
            .padding(20).frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.white).clipShape(RoundedRectangle(cornerRadius: 20))
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.cardBorder))
    }
}

private struct TrustTag: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        Text(text).font(.system(size: 10, weight: .bold)).foregroundColor(.brandBlue)
            .padding(.horizontal, 10).padding(.vertical, 9)
            .background(Color(hex: 0xEBF0FF)).clipShape(RoundedRectangle(cornerRadius: 30))
    }
}

private struct InfoCardRow: View {
    let l1: String, v1: String?, l2: String, v2: String?
    init(_ l1: String, _ v1: String?, _ l2: String, _ v2: String?) { self.l1=l1; self.v1=v1; self.l2=l2; self.v2=v2 }
    var body: some View {
        HStack(spacing: 12) {
            ResultInfoCard(l1, v1).frame(maxWidth: .infinity)
            ResultInfoCard(l2, v2).frame(maxWidth: .infinity)
        }
    }
}

private struct ResultInfoCard: View {
    let label: String, value: String?
    init(_ label: String, _ value: String?) { self.label = label; self.value = value }
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            CardLabel(label.uppercased())
            Text(value?.isEmpty == false ? value! : "—").font(.system(size: 17, weight: .heavy)).foregroundColor(.infoValue)
        }
        .padding(16).frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white).clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.cardBorder))
    }
}

private struct DeliveryStatusCard: View {
    let status: String
    var body: some View {
        let text = status.isEmpty ? "Status not available" : status
        let lower = text.lowercased()
        let delivered = lower.contains("deliver") && !lower.contains("not")
        let pending = lower.contains("not yet") || lower.contains("pending") || lower.contains("not deliver") || lower.contains("awaiting")
        let accent: Color = delivered ? .successGreen : (pending ? .pendingAmber : .brandBlue)
        return VStack(alignment: .leading, spacing: 10) {
            CardLabel("DELIVERY STATUS")
            Text(text).font(.system(size: 19, weight: .heavy)).foregroundColor(accent)
        }
        .padding(18).frame(maxWidth: .infinity, alignment: .leading)
        .background(accent.opacity(0.08)).clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(accent.opacity(0.3)))
    }
}

private struct CheckLine: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            ZStack {
                Circle().fill(Color(hex: 0xDCE6F7)).frame(width: 20, height: 20)
                Image(systemName: "checkmark").font(.system(size: 11, weight: .bold)).foregroundColor(.brandBlue)
            }
            Text(text).font(.system(size: 14)).foregroundColor(Color(hex: 0x44506B))
        }.padding(.top, 4)
    }
}

private struct StepRow: View {
    let n: Int, title: String, body_: String, last: Bool
    init(_ n: Int, _ title: String, _ body: String, last: Bool) { self.n=n; self.title=title; self.body_=body; self.last=last }
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            ZStack {
                Circle().fill(last ? Color.brandOrange : Color.brandBlue).frame(width: 26, height: 26)
                Text("\(n)").font(.system(size: 13, weight: .bold)).foregroundColor(.white)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text(title).font(.system(size: 15, weight: .bold)).foregroundColor(.brandBlue)
                Text(body_).font(.system(size: 13)).foregroundColor(.labelGrey)
            }
        }.padding(.top, 4)
    }
}

private struct CardLabel: View {
    let text: String
    init(_ text: String) { self.text = text }
    var body: some View { Text(text.uppercased()).font(.system(size: 11, weight: .heavy)).foregroundColor(.labelGrey) }
}

private struct CapsLabel: View {
    let text: String; let color: Color
    init(_ text: String, color: Color) { self.text = text; self.color = color }
    var body: some View { Text(text).font(.system(size: 11, weight: .heavy)).foregroundColor(color) }
}

private struct ErrorContent: View {
    let message: String, actionLabel: String, onAction: () -> Void
    var body: some View {
        VStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 8) {
                CardLabel("STATUS")
                HStack {
                    Image(systemName: "exclamationmark.circle").foregroundColor(.errorRed)
                    Text(message).font(.system(size: 17, weight: .bold)).foregroundColor(.errorRed)
                }
            }
            .padding(16).frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.errorRed.opacity(0.08)).clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.errorRed.opacity(0.35)))
            Button(action: onAction) {
                Text(actionLabel).foregroundColor(.errorRed)
                    .padding(.horizontal, 24).padding(.vertical, 10)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.errorRed.opacity(0.5)))
            }
        }
        .padding(16)
    }
}
