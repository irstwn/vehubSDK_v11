
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
    var temp_string_parameters = command.argument(at: 2) as? [AnyHashable : Any]
    self.commandId = command.callbackId
    if(temp_string_parameters == nil){
      string_parameters = ["see_straight":"See straight","close_eyes":"Close both your eyes","open_mouth":"Open your mouth","tilt_right":"Tilt to the right", "tilt_left":"Tilt to the left","see_below":"See below", "see_above":"See above", "see_right":"See your right", "see_left":"See your left"]
    }else{
      string_parameters = temp_string_parameters
    }
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
