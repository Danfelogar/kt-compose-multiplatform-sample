//
//  SimpleIOSButton.swift
//  iosApp
//
//  Created by Daniel on 19/08/26.
//

import SwiftUI
import Shared

class IOSNativeViewFactory: NativeViewFactory {
    static var shared = IOSNativeViewFactory()
    func createButtonView(label: String, onClick: @escaping () -> Void) -> UIViewController {
        return UIHostingController(rootView: SimpleIOSButton(label: label, action: onClick))
    }
}

struct SimpleIOSButton: View {
    var label: String
    var action: ()-> Void
    
    var body: some View {
        Button(action: action){
            Text(label)
                .font(.headline)
        }
    }
}

#Preview {
    SimpleIOSButton(
        label: "Hello from iOS", action: {}
    )
}
