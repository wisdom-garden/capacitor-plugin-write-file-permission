declare module '@capacitor/core' {
  interface PluginRegistry {
    WriteFilePermission: WriteFilePermissionPlugin;
  }
}

export type PermissionType = "WriteExternalStorage";

export interface WriteFilePermissionPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  check(options: { permissionName: PermissionType }): Promise<{ result: boolean }>;
  request(options: { permissionName: PermissionType }): Promise<{ result: boolean }>;
}
