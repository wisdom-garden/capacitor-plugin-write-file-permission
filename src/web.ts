import { WebPlugin } from '@capacitor/core';
import { WriteFilePermissionPlugin } from './definitions';

export class WriteFilePermissionWeb extends WebPlugin implements WriteFilePermissionPlugin {
  constructor() {
    super({
      name: 'WriteFilePermission',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async check(__options: { permissionName: string }): Promise<{ result: boolean }> {
    return {result: false};
  }

  async request(__options: { permissionName: string }): Promise<{ result: boolean }> {
    return {result: false};
  }
}

const WriteFilePermission = new WriteFilePermissionWeb();

export { WriteFilePermission };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(WriteFilePermission);
