import { WebPlugin } from '@capacitor/core';
import { PermissionType, WriteFilePermissionPlugin } from './definitions';

export class WriteFilePermissionWeb extends WebPlugin implements WriteFilePermissionPlugin {
  constructor() {
    super({
      name: 'WriteFilePermission',
      platforms: ['web'],
    });
  }

  async check(__options: { permissionName: PermissionType }): Promise<{ result: boolean }> {
    return {result: true};
  }

  async request(__options: { permissionName: PermissionType }): Promise<{ result: boolean }> {
    return {result: true};
  }

  async requestPostNotificationPermission(): Promise<{ result: boolean }> {
    return {result: true};
  }
}

const WriteFilePermission = new WriteFilePermissionWeb();

export { WriteFilePermission };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WriteFilePermission);
