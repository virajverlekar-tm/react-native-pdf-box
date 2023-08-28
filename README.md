# react-native-pdf-box

This is a wrapper of [PdfBox-Android](https://github.com/TomRoush/PdfBox-Android)

## Setup

Add to AndroidManifest.xml

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>
```

Create file res/xml/provider_paths.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path
        name="external_files"
        path="." />
</paths>
```

## Usage

Using async/await

```jsx
import PdfBox from "react-native-pdf-box";

// unlock pdf
try {
    let result = await PdfBox.unlockPdf('filepath', 'password');
    console.log(result);
} catch(error) {
    console.error(error);
};
```

Using then/catch

```jsx
import PdfBox from "react-native-pdf-box";

// unlock pdf
PdfBox.unlockPdf('filepath', 'password')
    .then(result => {
        console.log(result);
    })
    .catch(error => {
        console.error(error);
    });
```
