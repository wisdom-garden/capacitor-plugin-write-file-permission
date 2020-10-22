declare module '@capacitor/core' {
  interface PluginRegistry {
    WriteFilePermission: WriteFilePermissionPlugin;
  }
}

export interface WriteFilePermissionPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  check(options: { permissionName: string }): Promise<{ result: boolean }>;
  request(options: { permissionName: string }): Promise<{ result: boolean }>;
}
