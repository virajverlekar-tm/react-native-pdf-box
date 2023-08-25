# react-native-pdf-box

This is a wrapper of [PdfBox-Android](https://github.com/TomRoush/PdfBox-Android)

## Getting started

`$ npm install react-native-pdf-box --save`

or

`$ yarn add react-native-pdf-box`

## Usage

Basic usage

```javascript
import PdfBox from "react-native-pdf-box";

// unlock pdf
PdfBox.unlockPdf('filepath', 'password');
```

Or, you can display a fallback screen or overlay if auto-start permission is not available.

```jsx
import React, { useCallback, useEffect, useState } from "react";
import { Button, FlatList, Text, View } from "react-native";
import PdfBox from "react-native-pdf-box";

const MyApp = () => {
  const onPress = useCallback(() => {
    PdfBox.unlockPdf('filepath', 'password');
  }, []);

  return (
    <View>
      <Text>
        PDF Box
      </Text>
      <Button onPress={onPress}>Unlock PDF</Button>
    </View>
  );
};
```

## Running the example
1. `cd example`
2. `yarn`
3. `react-native run-android`
