Capacitor Android `android.permission.WRITE_EXTERNAL_STORAGE` permission sometimes requested at runtime. but Capacitor built in [Permission](https://capacitorjs.com/docs/apis/permissions) not support WRITE_EXTERNAL_STORAGE. so DIY.



# Install



```bash
yarn add capacitor-plugin-write-file-permission
```

or 

```bash
npm install capacitor-plugin-write-file-permission
```



# Usage
```javascript
import { Plugins } from "@capacitor/core";
import { WriteFilePermissionPlugin } from "capacitor-plugin-write-file-permission";


const WriteFilePermission = Plugins.WriteFilePermission as WriteFilePermissionPlugin;

const permission = await WriteFilePermission.check({ permissionName: "WriteExternalStorage" });

if (!permission.result) {
  await WriteFilePermission.request({ permissionName: "WriteExternalStorage" });
}

```
