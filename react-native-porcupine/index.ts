import NativePorcupine from './NativePorcupine';

export const initializePorcupine = async (
  keywordPath: string,
  modelPath: string,
  sensitivity: number = 0.5
) => {
  return NativePorcupine.initialize(keywordPath, modelPath, sensitivity);
};

export const startListening = async () => {
  return NativePorcupine.start();
};

export const stopListening = async () => {
  return NativePorcupine.stop();
};

export const destroyPorcupine = async () => {
  return NativePorcupine.delete();
};
