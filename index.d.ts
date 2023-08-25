declare module "react-native-pdf-box" {
  export function unlockPdf(filePath: string, password: string): Promise<boolean>;
}
