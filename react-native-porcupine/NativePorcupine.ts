import { TurboModuleRegistry } from 'react-native';
import type { TurboModule } from 'react-native';

export interface PorcupineModuleInterface extends TurboModule {
  initialize(keywordPath: string, modelPath: string, sensitivity: number): void;
  start(): void;
  stop(): void;
  delete(): void;
}

const NativePorcupine = TurboModuleRegistry.get<PorcupineModuleInterface>('PorcupineModule');

export default NativePorcupine!;
