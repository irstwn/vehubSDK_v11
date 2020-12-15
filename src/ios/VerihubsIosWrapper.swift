
import Foundation
import verihubs
import UIKit

@objc(VerihubsIosWrapper)
public class VerihubsIosWrapper :  CDVPlugin , VerihubsDelegate{

  var verisdk: VerihubsSDK!
  var resp: String!
  var instruction_count: Int!
  var timeout: Int!
  var commandId: String!
  var string_parameters: [AnyHashable : Any] = [:]

    public func setResponse(response_status: String)
    {
        self.resp = response_status
    }
    


  @objc
  func initClass(_ command: CDVInvokedUrlCommand) {

    verisdk = VerihubsSDK()
    var pluginResult:CDVPluginResult

    pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: "Class has been initialized")
    self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
  }


  @objc
  func verifyLiveness(_ command: CDVInvokedUrlCommand) {

    self.instruction_count = command.argument(at: 0) as! Int?
    self.timeout = command.argument(at: 1) as! Int?
    self.string_parameters = command.argument(at: 2) as! [AnyHashable : Any]
    self.commandId = command.callbackId
    // for i in 0...8{
    //     switch i{
    //       case 0:
    //         real_string_parameters["see_left"] = string_parameters[i]
    //         break;
    //       case 1:
    //         real_string_parameters["see_right"] = string_parameters[i]
    //         break;
    //       case 2:
    //         real_string_parameters["see_straight"] = string_parameters[i]
    //         break;
    //       case 3:
    //         real_string_parameters["see_above"] = string_parameters[i]
    //         break;
    //       case 4:
    //         real_string_parameters["see_below"] = string_parameters[i]
    //         break;
    //       case 5:
    //         real_string_parameters["tilt_left"] = string_parameters[i]
    //         break;
    //       case 6:
    //         real_string_parameters["tilt_right"] = string_parameters[i]
    //         break;
    //       case 7:
    //         real_string_parameters["open_mouth"] = string_parameters[i]
    //         break;
    //       case 8:
    //         real_string_parameters["close_eyes"] = string_parameters[i]
    //         break;
    //       default:
    //         break;
    //     }
    // }
    print("asd")
    print(string_parameters)
    verisdk.verifyLiveness(viewController:self.viewController, delegate:self, instruction_count: self.instruction_count, timeout: self.timeout, string_parameters: self.string_parameters)

  }

  public func onActivityResult(response_result: [AnyHashable : Any])
  {
      var pluginResult:CDVPluginResult
      pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: response_result)
      self.commandDelegate.send(pluginResult, callbackId: self.commandId)

  }

  @objc
  func getVersion(_ command: CDVInvokedUrlCommand) {

    var response_result: [AnyHashable : Any] = [:]

    let temp2 = ["version": "1.2.0"] as [AnyHashable : Any]

    response_result = Array(response_result.keys).reduce(temp2) { (dict, key) -> [AnyHashable:Any] in
        var dict = dict
        dict[key] = response_result[key]
        return dict
    }
    
    var pluginResult:CDVPluginResult
    pluginResult = CDVPluginResult.init(status: CDVCommandStatus_OK, messageAs: response_result)
    self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
  }
}
