#import "BatteryOptimizationModePlugin.h"
#if __has_include(<battery_optimization_mode/battery_optimization_mode-Swift.h>)
#import <battery_optimization_mode/battery_optimization_mode-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "battery_optimization_mode-Swift.h"
#endif

@implementation BatteryOptimizationModePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBatteryOptimizationModePlugin registerWithRegistrar:registrar];
}
@end
