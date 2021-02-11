
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
  var custom_instructions: [Int32] = []
  var attributes_check: [Bool] = []

  

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
    self.attributes_check = command.argument(at: 3) as! [Bool]
    self.custom_instructions = command.argument(at: 2) as! [Int32]
    self.commandId = command.callbackId
    string_parameters = ["see_straight":"Arahkan wajah ke depan","close_eyes":"Tutup mata selama 2 detik","open_mouth":"Buka mulut","tilt_right":"Miringkan kepala ke kanan", "tilt_left":"Miringkan kepala ke kiri","see_below":"Tundukkan kepala ke bawah", "see_above":"Arahkan wajah ke atas", "see_right":"Tengok ke kanan", "see_left":"Tengok ke kiri","follow_instruction":"Posisikan wajah ke frame","remove_mask":"Mohon lepaskan masker","remove_sunglasses":"Mohon lepaskan kacamata"]

    verisdk.verifyLiveness(viewController:self.viewController, delegate:self, instruction_count: self.instruction_count, custom_instructions: self.custom_instructions, attributes_check: self.attributes_check, timeout: self.timeout, string_parameters: self.string_parameters)

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

    let temp2 = ["version": "1.3.3"] as [AnyHashable : Any]

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
