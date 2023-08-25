# react-native-pdf-box

This is a wrapper of [PdfBox-Android](https://github.com/TomRoush/PdfBox-Android)

## Usage

Using async/await

```javascript
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
