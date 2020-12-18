import Flutter
import UIKit

public class SwiftBatteryOptimizationModePlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "battery_optimization_mode", binaryMessenger: registrar.messenger())
    let instance = SwiftBatteryOptimizationModePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result(false)
  }
}
