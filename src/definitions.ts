declare module '@capacitor/core' {
  interface PluginRegistry {
    WriteFilePermission: WriteFilePermissionPlugin;
  }
}

export type PermissionType = "WriteExternalStorage";

export interface WriteFilePermissionPlugin {
  check(options: { permissionName: PermissionType }): Promise<{ result: boolean }>;
  request(options: { permissionName: PermissionType }): Promise<{ result: boolean }>;
}
