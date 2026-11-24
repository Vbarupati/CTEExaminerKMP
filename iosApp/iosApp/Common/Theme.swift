import SwiftUI

enum Theme {
    // From Assets.xcassets/Colors (auto dark-mode)
    static let navy   = Color("Navy")
    static let orange = Color("Orange")
    static let green  = Color("Green")
    static let red    = Color("Red")

    // Literals (no asset color needed)
    static let navyAlt    = Color(red: 0x1B/255, green: 0x3C/255, blue: 0x8C/255)
    static let labelGrey  = Color(red: 0x4A/255, green: 0x5A/255, blue: 0x6A/255)
    static let shieldBg   = Color(red: 0xE3/255, green: 0xEC/255, blue: 0xFB/255)
    static let pageBg     = Color(red: 0xF5/255, green: 0xF7/255, blue: 0xFB/255)
    static let cardBorder = Color(red: 0xE2/255, green: 0xE6/255, blue: 0xEC/255)
}