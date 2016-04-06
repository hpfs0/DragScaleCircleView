# DragScaleCircleView
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DragScaleCircleView-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2966)

A custom imageview that provides the circle window can be dragged and scaled, crop image. 

## How does it look?
![image](https://github.com/hpfs0/DragScaleCircleView/blob/master/show.gif)

## Why?
Sometimes need to cut a picture into a circle.

## Install
The library is published on Jcenter:

```java
    compile 'com.rori.zenvo.dragscalecircleview:dragscalecircleview:1.0.1'
```

## Usage
To add the DragScaleCircleView to your application, specify com.rori.zenvo.dragscalecircleview.DragScaleCircleView in your layout XML.

```xml
    <com.rori.zenvo.dragscalecircleview.DragScaleCircleView
        android:id="@+id/dragScaleCircleView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:clickable="true"
        android:src="@drawable/img1"/>
```

## Customization
The default dragScaleCircleView to show the guideline.
If you want to guideline don't shown, please define the <code>xmlns:app="http://schemas.android.com/apk/res-auto"</code> on your layout file.

|name|format|description|
|:---:|:---:|:---:|
| hasGuideLine | boolean |set the flag of circle window's guide line diplay/not display
| guideLineSize | float |set the size of circle window's guide line
| guideLineColor | integer |set the color of circle window's guide line
| borderSize | float |set the size of circle window's border line
| borderColor | integer |set the color of circle window's border line

## Download
The latest version can be downloaded as a zip and referenced by your application as a library project.

## Thanks
* [android-crop](https://github.com/jdamcd/android-crop)
* [GalleryFinal](https://github.com/pengjianbo/GalleryFinal)

## TODO
- [ ] add background image selector from file system.
- [x] make the circle window move and drag on imageview.
- [x] fix move the circle window right edge bug.
- [x] impprove performence when the circle window move and drag.
- [x] crop the circle window from imageview.
- [x] cropped image can be saved/load.
- [x] add custom properties of guideline.(et. on/off、size、color).
- [x] add custom properties of border.(et. size、color).

## License

    Copyright 2015~2016 hpfs0

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
