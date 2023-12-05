import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(WriteFilePermission)
public class WriteFilePermission: CAPPlugin {
    @objc func check(_ call: CAPPluginCall) {
        // let permissionName = call.getString("permissionName") ?? ""
        call.resolve([
            "result": true
        ])
    }

    @objc func request(_ call: CAPPluginCall) {
        // let permissionName = call.getString("permissionName") ?? ""
        call.success([
            "result": true
        ])
    }

    @objc func requestPostNotificationPermission(_ call: CAPPluginCall) {
        // let permissionName = call.getString("permissionName") ?? ""
        call.success([
            "result": false
        ])
    }
}
