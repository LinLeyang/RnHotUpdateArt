'use strict';

import React from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  NativeModules,
  DeviceEventEmitter
} from 'react-native';

class HelloWorld extends React.Component {

  pressSelectContract() {
    // 调用Native页面
    NativeModules.R2NModule.handleMessage("I press button.", (msg) => {
      console.log(msg);
    });
  }
  componentWillMount() {
    DeviceEventEmitter.addListener('AndroidToRNMessage', this.handleAndroidMessage.bind(this));
  }

  handleAndroidMessage(androidMeg) {
    console.log(androidMeg);
  }


  render() {

    var initProps = this.props.bundle;
    return (
      <View style={styles.container}>
        <Text style={styles.hello} onPress={this.pressSelectContract}>BUS_A_patch</Text>
        <Text style={styles.hello}>{initProps}</Text>
      </View>


    )
  }
}
var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  hello: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});

AppRegistry.registerComponent('RnHotUpdateArt', () => HelloWorld);