'use strict';

var {Intent} = require('react-native').NativeModules;


var DiaFS = {
  startNativeIntent(PACKAGE_NAME, FILE_PATH){
      return Intent.startActivityForResult(PACKAGE_NAME, FILE_PATH);
  },
  pickImage(){
    return Intent.pickImage();
  }
}

module.exports = DiaFS;
