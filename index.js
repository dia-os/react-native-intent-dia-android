'use strict';

var {Intent} = require('react-native').NativeModules;



exports.startNativeIntent = (PACKAGE_NAME, FILE_PATH) => {
  return Intent.startActivityForResult(PACKAGE_NAME, FILE_PATH);
};
