import React, { useEffect, useState } from 'react';
import {
  Button,
  View,
  Text,
  Alert,
  StyleSheet,
  SafeAreaView,
  PermissionsAndroid,
  Platform,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';

// Import TurboModule functions from your JS wrapper
import {
  initializePorcupine,
  startListening,
  stopListening,
  destroyPorcupine,
} from '../react-native-porcupine'; // Update path if needed

const App = () => {
  const [status, setStatus] = useState<string>('🔌 Not initialized');
  const [isListening, setIsListening] = useState<boolean>(false);

  const requestMicrophonePermission = async () => {
    if (Platform.OS === 'android') {
      try {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
          {
            title: 'Microphone Permission',
            message: 'App needs access to your microphone to detect the wake word.',
            buttonNeutral: 'Ask Me Later',
            buttonNegative: 'Cancel',
            buttonPositive: 'OK',
          }
        );
        if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
          Alert.alert('Permission Denied', 'Microphone permission is required.');
          return false;
        }
      } catch (err) {
        console.warn(err);
        return false;
      }
    }
    return true;
  };

  useEffect(() => {
    const initPorcupine = async () => {
      const permissionGranted = await requestMicrophonePermission();
      if (!permissionGranted) return;

      try {
        const msg = await initializePorcupine(
          'keyword.ppn', // This file must be in android/app/src/main/assets
          'porcupine_params.pv', // Also in android/app/src/main/assets
          0.7
        );
        console.log('initializePorcupine msg:', msg);
        setStatus('✅ ' + msg);
      } catch (error: any) {
        console.error('Initialization error:', error);
        Alert.alert('Initialization Error', error.message || 'Unknown error');
        setStatus('❌ Initialization failed');
      }
    };

    initPorcupine();

    // Listen to native event for wake word detection
    const emitter = new NativeEventEmitter(NativeModules.PorcupineModule);
    const subscription = emitter.addListener('WakeWordDetected', () => {
      console.log('👂 Wake word detected');
      Alert.alert('Wake Word Detected', '👂 Wake word was detected!');
      setStatus('🚀 Wake word detected!');
    });

    return () => {
      subscription.remove();
      destroyPorcupine().catch(console.warn);
    };
  }, []);

  const handleStart = async () => {
    try {
      const msg = await startListening();
      console.log('startListening msg:', msg);
      setStatus('🎙️ ' + msg);
      setIsListening(true);
    } catch (error: any) {
      console.error('Start error:', error);
      Alert.alert('Start Error', error.message || 'Unknown error');
      setStatus('❌ Failed to start listening');
      setIsListening(false);
    }
  };

  const handleStop = async () => {
    try {
      const msg = await stopListening();
      console.log('stopListening msg:', msg);
      setStatus('⏹️ ' + msg);
      setIsListening(false);
    } catch (error: any) {
      console.error('Stop error:', error);
      Alert.alert('Stop Error', error.message || 'Unknown error');
      setStatus('❌ Failed to stop listening');
      setIsListening(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.status}>{status}</Text>
      <Text style={styles.status}>
        {isListening ? '🎧 Listening for wake word...' : '🔇 Not listening'}
      </Text>
      <View style={styles.spacer} />
      <Button title="▶️ Start Listening" onPress={handleStart} />
      <View style={styles.spacer} />
      <Button title="⏹️ Stop Listening" onPress={handleStop} />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 24,
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  status: {
    fontSize: 18,
    marginBottom: 12,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  spacer: {
    height: 16,
  },
});

export default App;
