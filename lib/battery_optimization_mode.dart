import 'dart:async';

import 'package:flutter/services.dart';

class BatteryOptimizationMode {
  static const MethodChannel _channel = const MethodChannel('battery_optimization_mode');

  static Future<bool> get isInPowerSaveMode => _channel.invokeMethod('isInPowerSaveMode');

  static Future<bool> get isIgnoringPowerSaveMode => _channel.invokeMethod('isIgnoringPowerSaveMode');

  static Future<void> get openBatterySettings => _channel.invokeMethod('openBatterySettings');
}
